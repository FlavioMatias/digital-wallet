package api.digital_wallet.shared.value;

import api.digital_wallet.shared.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public record Email(String address) {
    private static final String REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    public Email {
        if (address == null || !address.matches(REGEX)) {
            throw new BusinessException(
                    "E-mail inválido: " + address,
                    "INVALID_EMAIL",
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}