package api.digital_wallet.shared.exceptions;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        String code,
        String message,
        LocalDateTime timestamp,
        Map<String, String> details
) {
    public ErrorResponse(String code, String message) {
        this(code, message, LocalDateTime.now(), null);
    }
}