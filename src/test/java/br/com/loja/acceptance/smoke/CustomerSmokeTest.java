package br.com.loja.acceptance.smoke;

import br.com.loja.base.BaseTest;
import br.com.loja.factory.CustomerFactory;
import org.testng.annotations.Test;

public class CustomerSmokeTest extends BaseTest {
    @Test
    public void customerRegistrationEndpointIsAvailable() {
        customerService.register(CustomerFactory.validCustomer())
                .then()
                .statusCode(201);
    }
}
