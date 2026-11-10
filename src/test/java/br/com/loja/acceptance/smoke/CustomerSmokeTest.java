package br.com.loja.acceptance.smoke;

import br.com.loja.base.BaseTest;
import br.com.loja.factory.CustomerFactory;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CustomerSmokeTest extends BaseTest {
    @Test(testName = "[customer-registration-api-test][smoke] Registration endpoint is available - Success")
    public void customerRegistrationEndpointIsAvailable() {
        Response response = customerService.register(CustomerFactory.validCustomer());

        Assert.assertEquals(response.statusCode(), 201, "HTTP status. Body: " + response.asString());
    }
}
