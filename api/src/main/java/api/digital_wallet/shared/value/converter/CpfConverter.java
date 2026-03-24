package api.digital_wallet.shared.value.converter;

import api.digital_wallet.shared.value.Cpf;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CpfConverter implements AttributeConverter<Cpf, String> {

    @Override
    public String convertToDatabaseColumn(Cpf cpf) {
        return cpf != null ? cpf.value() : null;
    }

    @Override
    public Cpf convertToEntityAttribute(String dbData) {
        return dbData != null ? new Cpf(dbData) : null;
    }
}