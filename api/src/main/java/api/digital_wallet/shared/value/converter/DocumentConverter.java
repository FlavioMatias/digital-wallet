package api.digital_wallet.shared.value.converter;

import api.digital_wallet.shared.value.Document;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DocumentConverter implements AttributeConverter<Document, String> {

    @Override
    public String convertToDatabaseColumn(Document document) {
        return document != null ? document.value() : null;
    }

    @Override
    public Document convertToEntityAttribute(String dbData) {
        return dbData != null ? new Document(dbData) : null;
    }
}