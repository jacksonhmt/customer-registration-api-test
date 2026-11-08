package br.com.loja.services;

import br.com.loja.client.CustomerClient;
import br.com.loja.dto.CustomerRequest;
import io.restassured.response.Response;

public class CustomerService {
    private final CustomerClient customerClient;

    public CustomerService(CustomerClient customerClient) {
        this.customerClient = customerClient;
    }

    public Response register(CustomerRequest customer) {
        return customerClient.createCustomer(customer);
    }
}
