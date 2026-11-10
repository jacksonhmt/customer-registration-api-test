# Customer registration API tests

Java API automation example using Rest-Assured and TestNG. The tests use the
project's `client`, `dto`, `enums`, `factory`, and `services` layers, with
acceptance tests separated into `contract`, `functional`, and `smoke`.

## Structure

```text
src/main/java/br/com/loja/
├── client/    # HTTP calls and property loader
├── dto/       # Request payloads
├── enums/     # API enum values and JSON schema locations
├── factory/   # Test data builders
└── services/  # Business-level API operations

src/test/java/br/com/loja/
├── acceptance/
│   ├── contract/
│   ├── functional/
│   └── smoke/
└── base/      # Shared test setup
```

## API contract covered

### JSON Schema validation

Contract tests validate response bodies against JSON Schemas (draft-04,
`"$schema": "http://json-schema.org/draft-04/schema#"`) stored in
`src/main/resources/json_schemas/`, using the Rest-Assured matcher
`matchesJsonSchemaInClasspath`:

| Schema | Used for |
|---|---|
| `customer-create-response.json` | `201` success (`success`, `message`, `data.*`; `additionalProperties: false` also guarantees `cpf` is never exposed) |
| `error-response.json` | `409` duplicated customer and `400` validation errors (`success`, `code`, `message`, `path`, optional `errors[]`) |

```java
assertThat(response.asString(), matchesJsonSchemaInClasspath(CUSTOMER_CREATE_RESPONSE.classpathLocation()));
```

To cover a new response, add a schema file there, register it in
`enums/JsonSchema` and assert it in the contract test. Schemas are strict: a new
field added by the backend must be added to the schema as well.

### Response format

`POST /api/v1/customers` returns `201` with:

```json
{ "success": true, "message": "...", "data": { "id": 1, "fullName": "...", "address": { } } }
```

- Customer fields are under `data.*`; the CPF is **not** returned.
- The backend normalizes data: CEP and phones are stored as digits only
  (`01310-100` → `01310100`, `(11) 99999-9999` → `11999999999`), the e-mail is
  lower-cased, and a `null` `additionalPhone` is omitted from the response.
- `CustomerFactory` generates a valid CPF (check digits) and a unique e-mail on
  every call, so runs never conflict with data left by previous executions.

## Run locally

Requires Java 27 and a running customer API. Use a **temporary** database, not
a database you care about. Example with Docker (port `55432` avoids clashing
with a local PostgreSQL on `5432`):

```shell
docker run -d --name pg-api-test -e POSTGRES_DB=loja -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=temporary-password -p 127.0.0.1:55432:5432 postgres:17

# in the backend repository
./mvnw -DskipTests package
DB_URL=jdbc:postgresql://localhost:55432/loja DB_USERNAME=postgres \
DB_PASSWORD=temporary-password SPRING_FLYWAY_ENABLED=true \
java -jar target/customer-registration-api-0.0.1-SNAPSHOT.jar
```

Then, in this repository (the API defaults to `http://localhost:8080`):

```shell
./mvnw test                                  # all stages
API_BASE_URL=http://localhost:8080 ./mvnw test
```

All URLs and paths live in `src/main/resources/api.properties`
(`api.base.url`, `api.customers.path`). Override any of them with a system
property (`-Dapi.base.url=...`) or an environment variable (`API_BASE_URL`).

### Running each stage

Each filter fails when no test matches it:

```shell
./mvnw test -Dtest='br/com/loja/acceptance/smoke/**'      -Dsurefire.failIfNoSpecifiedTests=true
./mvnw test -Dtest='br/com/loja/acceptance/functional/**' -Dsurefire.failIfNoSpecifiedTests=true
./mvnw test -Dtest='br/com/loja/acceptance/contract/**'   -Dsurefire.failIfNoSpecifiedTests=true
```

Clean up with `docker rm -f pg-api-test`.

## Pipeline (`.github/workflows/tests.yml`)

Runs on push to `main`, pull requests, manual dispatch (`workflow_dispatch`) and
as a reusable workflow (`workflow_call`). Everything happens in one job on
`ubuntu-latest` (Java 27 Temurin, Maven wrapper, dependency cache), so
`localhost` and the backend stay available between stages:

1. Check out the backend (`backend_ref` when called by the backend, `main` on
   direct runs) and this automation project:
   - called by the backend: this repository's `main`;
   - direct run (push, manual): the commit that triggered the workflow;
   - pull request: the **head commit of the source branch**, taken from the
     repository that owns it (`github.event.pull_request.head.repo`), so PRs
     from forks work too. It is *not* the merge commit with `main`
     (`refs/pull/N/merge`), so a PR is tested exactly as its author pushed it.
2. Start a **temporary PostgreSQL 17** service container with a health check and
   throwaway credentials. No local or production database is ever used.
3. Build the backend and start it with `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
   and `SPRING_FLYWAY_ENABLED=true` (Flyway creates the schema).
4. Wait up to ~3 minutes for `GET /api/v1/customers` to answer. If the process
   dies or the API never starts, the backend log is printed and the job fails.
5. Run **smoke → functional → contract** against `API_BASE_URL`
   (`http://localhost:8080`), stopping at the first failing stage.
6. Upload the Surefire reports of each stage as its own artifact
   (`reports-1-smoke`, `reports-2-functional`, `reports-3-contract`), also when a
   stage fails. The application JAR is not published.

Permissions are limited to `contents: read`.

> The caller references `...tests.yml@main`, so this workflow file must exist on
> the `main` branch of this repository before the backend can use it.

### Calling it from the backend

In `jacksonhmt/customer-registration-api`:

```yaml
jobs:
  api-automation:
    permissions:
      contents: read
    uses: jacksonhmt/customer-registration-api-test/.github/workflows/tests.yml@main
    with:
      backend_ref: ${{ github.sha }}
    # secrets:
    #   automation_token: ${{ secrets.AUTOMATION_REPO_TOKEN }}   # only if the automation repo is private
```

### Access between repositories

Both repositories are currently public, so the default `GITHUB_TOKEN` is enough
and no secret is required. If either becomes private (never put token values in
the repository; create them as Actions secrets):

- **Automation repo private:** in its *Settings → Actions → General → Access*,
  allow repositories of the same owner to use its workflows, otherwise the
  backend cannot call it.
- **Called by the backend, automation private:** the caller's `GITHUB_TOKEN`
  reads the backend, but not this repo. Create a fine-grained PAT with
  `Contents: read` on this repo, store it as a secret in the backend and pass it
  as `automation_token` (or use `secrets: inherit`).
- **Direct runs here, backend private:** store a PAT with `Contents: read` on the
  backend repo as the secret `backend_token` in this repository. When the backend
  calls the workflow, no extra secret is needed to read the backend, because the
  token inside a reusable workflow belongs to the calling repository.
- Secrets are not exposed to pull requests from forks; the fallback to the
  default token works only for public repositories.
