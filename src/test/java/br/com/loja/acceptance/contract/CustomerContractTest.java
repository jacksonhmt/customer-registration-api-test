package br.com.loja.acceptance.contract;

import br.com.loja.base.BaseTest;
import br.com.loja.dto.CustomerRequest;
import br.com.loja.factory.CustomerFactory;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CustomerContractTest extends BaseTest {
    @Test
    public void createCustomerReturnsExpectedContract() {
        CustomerRequest customer = CustomerFactory.validCustomer();

        Response response = customerService.register(customer);

        response.then()
                .statusCode(201)
                .contentType("application/json")
                .body("id", notNullValue())
                .body("fullName", equalTo(customer.fullName()))
                .body("cpf", equalTo(customer.cpf()))
                .body("email", equalTo(customer.email()))
                .body("address.zipCode", equalTo(customer.address().zipCode()));
    }
}
