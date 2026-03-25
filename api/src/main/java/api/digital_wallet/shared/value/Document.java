package api.digital_wallet.shared.value;

import api.digital_wallet.shared.exceptions.BusinessException;
import org.springframework.http.HttpStatus;
import java.util.Objects;

public record Document(String value) {

    public Document {
        Objects.requireNonNull(value, "Documento não pode ser nulo");
        String cleanedValue = value.replaceAll("\\D", "");

        if (!isValid(cleanedValue)) {
            throw new BusinessException(
                    "O documento informado é inválido: " + value,
                    "INVALID_DOCUMENT_FORMAT",
                    HttpStatus.BAD_REQUEST
            );
        }

        value = cleanedValue;
    }

    private boolean isValid(String doc) {
        if (doc.length() == 11) return isValidCpf(doc);
        if (doc.length() == 14) return isValidCnpj(doc);
        return false;
    }

    private boolean isValidCpf(String cpf) {
        if (cpf.matches("(\\d)\\1{10}")) return false;
        int d1 = calculateDigit(cpf.substring(0, 9), new int[]{10, 9, 8, 7, 6, 5, 4, 3, 2});
        int d2 = calculateDigit(cpf.substring(0, 9) + d1, new int[]{11, 10, 9, 8, 7, 6, 5, 4, 3, 2});
        return cpf.equals(cpf.substring(0, 9) + d1 + d2);
    }

    private boolean isValidCnpj(String cnpj) {
        if (cnpj.matches("(\\d)\\1{13}")) return false;
        int d1 = calculateDigit(cnpj.substring(0, 12), new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        int d2 = calculateDigit(cnpj.substring(0, 12) + d1, new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        return cnpj.equals(cnpj.substring(0, 12) + d1 + d2);
    }

    private int calculateDigit(String str, int[] weight) {
        int sum = 0;
        for (int i = str.length() - 1; i >= 0; i--) {
            sum += Integer.parseInt(str.substring(i, i + 1)) * weight[weight.length - str.length() + i];
        }
        sum = 11 - sum % 11;
        return sum > 9 ? 0 : sum;
    }

    @Override
    public String toString() {
        if (value.length() == 11) {
            return value.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }
        return value.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }
}