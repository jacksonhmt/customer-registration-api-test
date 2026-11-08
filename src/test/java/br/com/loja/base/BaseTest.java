package br.com.loja.base;

import br.com.loja.client.CustomerClient;
import br.com.loja.services.CustomerService;
import org.testng.annotations.BeforeClass;

public abstract class BaseTest {
    protected CustomerService customerService;

    @BeforeClass
    public void setUp() {
        customerService = new CustomerService(new CustomerClient());
    }
}
