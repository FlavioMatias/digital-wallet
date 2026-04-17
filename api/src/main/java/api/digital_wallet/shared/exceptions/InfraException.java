package api.digital_wallet.shared.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InfraException extends RuntimeException {
    private final String code;
    private final HttpStatus status;

    public InfraException(String message, String code, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }
}