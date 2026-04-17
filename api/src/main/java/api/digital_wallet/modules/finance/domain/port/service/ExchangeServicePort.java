package api.digital_wallet.modules.finance.domain.port.service;

import api.digital_wallet.modules.finance.domain.enums.CurrencyType;
import api.digital_wallet.shared.value.Money;

public interface ExchangeServicePort {
    Money convert(Money amount, CurrencyType from, CurrencyType to);
}
