package br.com.loja.factory;

import br.com.loja.dto.AddressRequest;
import br.com.loja.dto.CustomerRequest;
import br.com.loja.enums.Gender;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class CustomerFactory {
    private CustomerFactory() {
    }

    public static CustomerRequest validCustomer() {
        return new CustomerRequest(
                "Maria da Silva",
                formattedCpf(),
                "maria-" + UUID.randomUUID() + "@example.com",
                "(11) 99999-9999",
                null,
                "1990-05-20",
                Gender.FEMALE,
                new AddressRequest(
                        "01310-100",
                        "Avenida Paulista",
                        "1000",
                        "Apto 12",
                        "Bela Vista",
                        "São Paulo",
                        "SP"
                )
        );
    }

    private static String formattedCpf() {
        int[] digits = new int[11];
        do {
            for (int i = 0; i < 9; i++) {
                digits[i] = ThreadLocalRandom.current().nextInt(10);
            }
        } while (allDigitsEqual(digits));

        digits[9] = checkDigit(digits, 9);
        digits[10] = checkDigit(digits, 10);
        return "%d%d%d.%d%d%d.%d%d%d-%d%d".formatted(
                digits[0], digits[1], digits[2],
                digits[3], digits[4], digits[5],
                digits[6], digits[7], digits[8],
                digits[9], digits[10]
        );
    }

    private static boolean allDigitsEqual(int[] digits) {
        for (int i = 1; i < 9; i++) {
            if (digits[i] != digits[0]) {
                return false;
            }
        }
        return true;
    }

    private static int checkDigit(int[] digits, int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += digits[i] * (length + 1 - i);
        }
        int remainder = (sum * 10) % 11;
        return remainder == 10 ? 0 : remainder;
    }
}
