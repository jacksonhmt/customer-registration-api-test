package br.com.loja.acceptance.functional;

import br.com.loja.base.BaseTest;
import br.com.loja.dto.CustomerRequest;
import br.com.loja.factory.CustomerFactory;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;

public class CustomerFunctionalTest extends BaseTest {
    @Test
    public void customerCanBeRegisteredWithAllFields() {
        CustomerRequest customer = CustomerFactory.validCustomer();

        Response response = customerService.register(customer);

        response.then()
                .statusCode(201)
                .body("fullName", equalTo(customer.fullName()))
                .body("cpf", equalTo(customer.cpf()))
                .body("email", equalTo(customer.email()))
                .body("phoneNumber", equalTo(customer.phoneNumber()))
                .body("additionalPhone", equalTo(customer.additionalPhone()))
                .body("birthDate", equalTo(customer.birthDate()))
                .body("gender", equalTo(customer.gender().name()))
                .body("address.zipCode", equalTo(customer.address().zipCode()))
                .body("address.street", equalTo(customer.address().street()))
                .body("address.number", equalTo(customer.address().number()))
                .body("address.complement", equalTo(customer.address().complement()))
                .body("address.neighborhood", equalTo(customer.address().neighborhood()))
                .body("address.city", equalTo(customer.address().city()))
                .body("address.state", equalTo(customer.address().state()));
    }
}
