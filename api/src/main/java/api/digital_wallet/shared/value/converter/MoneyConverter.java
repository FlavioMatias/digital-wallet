package api.digital_wallet.shared.value.converter;

import api.digital_wallet.shared.value.Money;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.math.BigDecimal;

@Converter(autoApply = true)
public class MoneyConverter implements AttributeConverter<Money, BigDecimal> {

    @Override
    public BigDecimal convertToDatabaseColumn(Money money) {
        return money != null ? money.amount() : null;
    }

    @Override
    public Money convertToEntityAttribute(BigDecimal dbData) {
        return dbData != null ? new Money(dbData) : null;
    }
}