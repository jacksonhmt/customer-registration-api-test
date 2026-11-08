package br.com.loja.dto;

public record AddressRequest(
        String zipCode,
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state
) {
}
