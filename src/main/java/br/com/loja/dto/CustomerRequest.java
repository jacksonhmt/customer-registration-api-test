package br.com.loja.dto;

import br.com.loja.enums.Gender;

public record CustomerRequest(
        String fullName,
        String cpf,
        String email,
        String phoneNumber,
        String additionalPhone,
        String birthDate,
        Gender gender,
        AddressRequest address
) {
}
