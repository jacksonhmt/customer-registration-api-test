package br.com.loja.acceptance.functional;

import br.com.loja.base.BaseTest;
import br.com.loja.dto.CustomerRequest;
import br.com.loja.factory.CustomerFactory;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class CustomerFunctionalTest extends BaseTest {

    @Test(testName = "[customer-registration-api-test][functional] Customer can be registered with all fields - Success")
    public void customerCanBeRegisteredWithAllFields() {
        CustomerRequest customer = CustomerFactory.validCustomer();

        Response response = customerService.register(customer);

        JsonPath json = response.jsonPath();

        SoftAssert softly = new SoftAssert();
        softly.assertEquals(response.statusCode(), 201, "HTTP status");
        softly.assertEquals(json.getBoolean("success"), true, "success");
        softly.assertNotNull(json.get("data.id"), "data.id");
        softly.assertEquals(json.getString("data.fullName"), customer.fullName(), "data.fullName");
        softly.assertEquals(json.getString("data.email"), customer.email().toLowerCase(), "data.email");
        softly.assertEquals(json.getString("data.phoneNumber"), digitsOnly(customer.phoneNumber()), "data.phoneNumber");
        softly.assertFalse(json.getMap("data").containsKey("additionalPhone"), "additionalPhone must be omitted when null");
        softly.assertEquals(json.getString("data.birthDate"), customer.birthDate(), "data.birthDate");
        softly.assertEquals(json.getString("data.gender"), customer.gender().name(), "data.gender");
        softly.assertEquals(json.getString("data.address.zipCode"), digitsOnly(customer.address().zipCode()), "data.address.zipCode");
        softly.assertEquals(json.getString("data.address.street"), customer.address().street(), "data.address.street");
        softly.assertEquals(json.getString("data.address.number"), customer.address().number(), "data.address.number");
        softly.assertEquals(json.getString("data.address.complement"), customer.address().complement(), "data.address.complement");
        softly.assertEquals(json.getString("data.address.neighborhood"), customer.address().neighborhood(), "data.address.neighborhood");
        softly.assertEquals(json.getString("data.address.city"), customer.address().city(), "data.address.city");
        softly.assertEquals(json.getString("data.address.state"), customer.address().state(), "data.address.state");
        softly.assertFalse(json.getMap("data").containsKey("cpf"), "data must not expose cpf");
        softly.assertAll();
    }

    @Test(testName = "[customer-registration-api-test][functional] Duplicated CPF is rejected - Conflict")
    public void duplicatedCpfIsRejected() {
        CustomerRequest customer = CustomerFactory.validCustomer();
        Response first = customerService.register(customer);
        Assert.assertEquals(first.statusCode(), 201, "First registration status. Body: " + first.asString());

        CustomerRequest sameCpf = CustomerFactory.validCustomer(customer.cpf());

        Response duplicate = customerService.register(sameCpf);

        Assert.assertEquals(duplicate.statusCode(), 409, "Duplicated CPF status. Body: " + duplicate.asString());
    }

    private static String digitsOnly(String value) {
        return value.replaceAll("\\D", "");
    }
}
