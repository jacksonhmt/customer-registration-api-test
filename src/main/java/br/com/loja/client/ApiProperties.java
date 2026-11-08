package br.com.loja.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ApiProperties {
    private static final String FILE = "api.properties";
    private static final Properties PROPERTIES = load();

    private ApiProperties() {
    }

    // Precedence: system property, environment variable (dots as underscores), properties file
    public static String get(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = System.getenv(key.replace('.', '_').toUpperCase());
        }
        if (value == null) {
            value = PROPERTIES.getProperty(key);
        }
        if (value == null) {
            throw new IllegalStateException("Missing property: " + key);
        }
        return value;
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream input = ApiProperties.class.getClassLoader().getResourceAsStream(FILE)) {
            if (input == null) {
                throw new IllegalStateException(FILE + " not found on classpath");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load " + FILE, e);
        }
        return properties;
    }
}
