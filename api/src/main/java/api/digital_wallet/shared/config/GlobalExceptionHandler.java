package api.digital_wallet.shared.config;

import api.digital_wallet.modules.finance.exception.FinanceDomainException;
import api.digital_wallet.shared.exceptions.BusinessException;
import api.digital_wallet.shared.exceptions.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FinanceDomainException.class)
    public ResponseEntity<ErrorResponse> handleFinanceDomain(FinanceDomainException ex) {
        log.warn("Finance Domain Violation: {} [Code: {}]", ex.getMessage(), ex.getCode());

        // Aqui está a mágica: Traduzimos o código interno para o status do Front
        HttpStatus status = switch (ex.getCode()) {
            case "FIN-001" -> HttpStatus.UNPROCESSABLE_CONTENT; // Saldo insuficiente (422)
            case "FIN-002" -> HttpStatus.FORBIDDEN;              // Wallet Bloqueada (403)
            case "FIN-003" -> HttpStatus.BAD_REQUEST;            // Moeda incompatível (400)
            case "FIN-504" -> HttpStatus.GATEWAY_TIMEOUT;        // Timeout (504)
            default -> HttpStatus.BAD_REQUEST;                   // Erro genérico (400)
        };

        var response = new ErrorResponse(ex.getCode(), ex.getMessage());
        return ResponseEntity.status(status).body(response);
    }

    // Handler para as outras BusinessExceptions que já trazem o status (como a de Integração)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        log.error("Business Error: {} [Code: {}]", ex.getMessage(), ex.getCode());

        var response = new ErrorResponse(ex.getCode(), ex.getMessage());
        // Se o status for null, caímos em um fallback seguro (ex: 400)
        HttpStatus status = ex.getStatus() != null ? ex.getStatus() : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(response);
    }

    // (@Valid, @NotNull, etc)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        var response = new ErrorResponse("VALIDATION_ERROR", "Dados inválidos",
                java.time.LocalDateTime.now(), errors);
        return ResponseEntity.badRequest().body(response);
    }

    // (Banco fora, NullPointer inesperado)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unexpected error occurred", ex);
        var response = new ErrorResponse("INTERNAL_SERVER_ERROR", "Ocorreu um erro inesperado no servidor");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}