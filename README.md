# Customer registration API tests

Java API automation example using Rest-Assured and TestNG. The tests use the
project's `client`, `dto`, `enums`, `factory`, and `services` layers, with
acceptance tests separated into `contract`, `functional`, and `smoke`.

## Structure

```text
src/main/java/br/com/loja/
├── client/    # HTTP calls and property loader
├── dto/       # Request payloads
├── enums/     # API enum values
├── factory/   # Test data builders
└── services/  # Business-level API operations

src/test/java/br/com/loja/
├── acceptance/
│   ├── contract/
│   ├── functional/
│   └── smoke/
└── base/      # Shared test setup
```

## Run

Start the customer API, then run:

```shell
mvn test
```

All URLs and paths live in `src/main/resources/api.properties`
(`api.base.url`, `api.customers.path`). Override any of them with a system
property or an environment variable:

```shell
mvn test -Dapi.base.url=http://localhost:8080
API_BASE_URL=http://localhost:8080 mvn test
```

The contract example assumes customer creation returns HTTP `201`, JSON, and
a response object containing `id`, `fullName`, `cpf`, `email`, and
`address.zipCode`. The functional example assumes the response echoes the
submitted customer fields. Align these response assertions if the API's
documented response differs.
