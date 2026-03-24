package api.digital_wallet.modules.finance.domain.enums;

import lombok.Getter;

@Getter
public enum CurrencyType {
    BRL(5.20),
    USD(1.00),
    EUR(0.92);

    private final double factor;

    CurrencyType(double factor) {
        this.factor = factor;
    }

}