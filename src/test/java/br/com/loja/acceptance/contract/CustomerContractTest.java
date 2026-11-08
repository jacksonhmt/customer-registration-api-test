package br.com.loja.acceptance.contract;

import br.com.loja.base.BaseTest;
import br.com.loja.dto.CustomerRequest;
import br.com.loja.factory.CustomerFactory;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.notNullValue;

public class CustomerContractTest extends BaseTest {
    @Test
    public void createCustomerReturnsExpectedContract() {
        CustomerRequest customer = CustomerFactory.validCustomer();

        Response response = customerService.register(customer);

        response.then()
                .statusCode(201)
                .contentType("application/json")
                .body("success", equalTo(true))
                .body("message", not(emptyOrNullString()))
                .body("data.id", instanceOf(Number.class))
                .body("data.fullName", equalTo(customer.fullName()))
                .body("data.email", equalTo(customer.email()))
                .body("data.phoneNumber", instanceOf(String.class))
                .body("data.birthDate", equalTo(customer.birthDate()))
                .body("data.gender", equalTo(customer.gender().name()))
                .body("data.createdAt", notNullValue())
                .body("data.address.zipCode", instanceOf(String.class))
                .body("data.address.street", equalTo(customer.address().street()))
                .body("data.address.state", equalTo(customer.address().state()))
                .body("data", not(hasKey("cpf")));
    }
}
