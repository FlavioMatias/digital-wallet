package api.digital_wallet.modules.finance.domain.service;

import api.digital_wallet.modules.finance.domain.enums.CurrencyType;
import api.digital_wallet.modules.finance.domain.port.service.ExchangeServicePort;
import api.digital_wallet.shared.value.Money;
import org.springframework.stereotype.Service;

@Service
public class ExchangeService implements ExchangeServicePort {

    public Money convert(Money amount, CurrencyType from, CurrencyType to) {
        if (from == to) return amount;

        double rate = getRate(from, to);
        return amount.multiply(rate);
    }

    private double getRate(CurrencyType from, CurrencyType to) {
        return to.getFactor() / from.getFactor();
    }
}