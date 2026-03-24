package api.digital_wallet.shared.config;

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

    // (Saldo insuficiente, CPF já cadastrado, etc)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        log.warn("Business error: {} [Code: {}]", ex.getMessage(), ex.getCode());
        var response = new ErrorResponse(ex.getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(response);
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