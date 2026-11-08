package br.com.loja.client;

import br.com.loja.dto.CustomerRequest;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CustomerClient {
    private final String baseUrl = ApiProperties.get("api.base.url");
    private final String customersPath = ApiProperties.get("api.customers.path");

    public Response createCustomer(CustomerRequest customer) {
        return given()
                .baseUri(baseUrl)
                .contentType("application/json")
                .accept("application/json")
                .body(customer)
                .when()
                .post(customersPath);
    }
}
