package api.digital_wallet.shared.value;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal amount) {

    public Money {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        amount = amount.setScale(2, RoundingMode.HALF_EVEN);
    }

    public static Money of(double value) {
        return new Money(BigDecimal.valueOf(value));
    }

    public static Money zero() {return new Money(java.math.BigDecimal.ZERO);}

    public boolean isGreaterThanZero() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isLessThan(Money other) {
        return this.amount.compareTo(other.amount) < 0;
    }

    public boolean isLessThanOrEqualToZero() {return this.amount.compareTo(java.math.BigDecimal.ZERO) <= 0;}

    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money multiply(double factor) {return new Money(this.amount.multiply(BigDecimal.valueOf(factor)));}

    public BigDecimal getAmount() {return amount;}

    public boolean isZero() {return this.amount.compareTo(BigDecimal.ZERO) == 0;}
}