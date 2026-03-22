package api.digital_wallet.shared.value;
import api.digital_wallet.shared.exceptions.BusinessException;
import org.springframework.http.HttpStatus;
import java.util.Objects;

public record Cpf(String value) {

    public Cpf {
        Objects.requireNonNull(value, "CPF não pode ser nulo");
        String cleanedValue = value.replaceAll("\\D", "");

        if (!isValid(cleanedValue)) {
            throw new BusinessException(
                    "O CPF informado é inválido: " + value,
                    "INVALID_CPF_FORMAT",
                    HttpStatus.BAD_REQUEST
            );
        }

        value = cleanedValue; // Armazena apenas os números
    }

    private boolean isValid(String cpf) {
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int d1 = calculateDigit(cpf.substring(0, 9), 10);
            int d2 = calculateDigit(cpf.substring(0, 9) + d1, 11);
            return cpf.equals(cpf.substring(0, 9) + d1 + d2);
        } catch (Exception e) {
            return false;
        }
    }

    private int calculateDigit(String base, int weight) {
        int sum = 0;
        for (int i = 0; i < base.length(); i++) {
            sum += Character.getNumericValue(base.charAt(i)) * weight--;
        }
        int rest = sum % 11;
        return rest < 2 ? 0 : 11 - rest;
    }

    @Override
    public String toString() {
        // Retorna formatado 000.000.000-00 apenas para exibição
        return value.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
}