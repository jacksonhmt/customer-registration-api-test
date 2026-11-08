package br.com.loja.acceptance.functional;

import br.com.loja.base.BaseTest;
import br.com.loja.dto.CustomerRequest;
import br.com.loja.factory.CustomerFactory;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.notNullValue;

public class CustomerFunctionalTest extends BaseTest {
    @Test
    public void customerCanBeRegisteredWithAllFields() {
        CustomerRequest customer = CustomerFactory.validCustomer();

        Response response = customerService.register(customer);

        // The backend stores CEP and phones as digits only and lowercases the e-mail
        response.then()
                .statusCode(201)
                .body("success", equalTo(true))
                .body("data.id", notNullValue())
                .body("data.fullName", equalTo(customer.fullName()))
                .body("data.email", equalTo(customer.email().toLowerCase()))
                .body("data.phoneNumber", equalTo(digitsOnly(customer.phoneNumber())))
                .body("data", not(hasKey("additionalPhone")))
                .body("data.birthDate", equalTo(customer.birthDate()))
                .body("data.gender", equalTo(customer.gender().name()))
                .body("data.address.zipCode", equalTo(digitsOnly(customer.address().zipCode())))
                .body("data.address.street", equalTo(customer.address().street()))
                .body("data.address.number", equalTo(customer.address().number()))
                .body("data.address.complement", equalTo(customer.address().complement()))
                .body("data.address.neighborhood", equalTo(customer.address().neighborhood()))
                .body("data.address.city", equalTo(customer.address().city()))
                .body("data.address.state", equalTo(customer.address().state()))
                .body("data", not(hasKey("cpf")));
    }

    @Test
    public void duplicatedCpfIsRejected() {
        CustomerRequest customer = CustomerFactory.validCustomer();
        customerService.register(customer).then().statusCode(201);

        CustomerRequest sameCpf = CustomerFactory.validCustomer(customer.cpf());

        customerService.register(sameCpf)
                .then()
                .statusCode(409);
    }

    private static String digitsOnly(String value) {
        return value.replaceAll("\\D", "");
    }
}
