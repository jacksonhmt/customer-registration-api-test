package br.com.loja.acceptance.contract;

import br.com.loja.base.BaseTest;
import br.com.loja.dto.CustomerRequest;
import br.com.loja.factory.CustomerFactory;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;

public class CustomerContractTest extends BaseTest {
    @Test(testName = "[customer-registration-api-test][contract] Create response matches JSON schema - Success")
    public void createCustomerResponseMatchesJsonSchema() {
        Response response = customerService.register(CustomerFactory.validCustomer());

        Assert.assertEquals(response.statusCode(), 201, "HTTP status. Body: " + response.asString());
        assertThat(response.asString(), matchesJsonSchemaInClasspath("json_schemas/customer-create-response.json"));
    }

    @Test(testName = "[customer-registration-api-test][contract] Duplicated customer error matches JSON schema - Conflict")
    public void duplicatedCustomerErrorMatchesJsonSchema() {
        CustomerRequest customer = CustomerFactory.validCustomer();
        Response first = customerService.register(customer);
        Assert.assertEquals(first.statusCode(), 201, "First registration status. Body: " + first.asString());

        Response duplicate = customerService.register(CustomerFactory.validCustomer(customer.cpf()));

        Assert.assertEquals(duplicate.statusCode(), 409, "HTTP status. Body: " + duplicate.asString());
        assertThat(duplicate.asString(), matchesJsonSchemaInClasspath("json_schemas/error-response.json"));
    }

    @Test(testName = "[customer-registration-api-test][contract] Validation error matches JSON schema - Bad Request")
    public void validationErrorMatchesJsonSchema() {
        CustomerRequest valid = CustomerFactory.validCustomer();
        CustomerRequest invalid = new CustomerRequest(
                "", valid.cpf(), "not-an-email", valid.phoneNumber(), null,
                valid.birthDate(), valid.gender(), valid.address());

        Response response = customerService.register(invalid);

        Assert.assertEquals(response.statusCode(), 400, "HTTP status. Body: " + response.asString());
        assertThat(response.asString(), matchesJsonSchemaInClasspath("json_schemas/error-response.json"));
    }
}
