package api.digital_wallet.unit.modules.finance.domain.service;

import api.digital_wallet.modules.finance.domain.enums.CurrencyType;
import api.digital_wallet.modules.finance.domain.service.ExchangeService;
import api.digital_wallet.shared.value.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Unit Test: Exchange Service")
class ExchangeServiceTest {

    private ExchangeService exchangeService;

    @BeforeEach
    void setUp() {
        exchangeService = new ExchangeService();
    }

    @Test
    @DisplayName("Should return same amount when currencies are identical")
    void shouldReturnSameAmountWhenCurrenciesAreIdentical() {
        Money amount = Money.of(100);
        Money result = exchangeService.convert(amount, CurrencyType.BRL, CurrencyType.BRL);
        assertThat(result.getAmount()).isEqualByComparingTo("100.00");
    }

    @ParameterizedTest
    @CsvSource({
            "BRL, USD, 5.00, 1.00",   // 1.0 / 5.0 = 0.2 rate -> 5 * 0.2 = 1
            "USD, BRL, 1.00, 5.00",   // 5.0 / 1.0 = 5.0 rate -> 1 * 5.0 = 5
            "USD, EUR, 100.00, 92.00", // 0.92 / 1.0 = 0.92 rate -> 100 * 0.92 = 92
            "BRL, EUR, 100.00, 18.40"  // 0.92 / 5.0 = 0.184 rate -> 100 * 0.184 = 18.40
    })
    @DisplayName("Should convert amount correctly based on currency factors")
    void shouldConvertCorrectlyBetweenCurrencies(
            CurrencyType from,
            CurrencyType to,
            double input,
            double expected) {

        Money amount = Money.of(input);
        Money result = exchangeService.convert(amount, from, to);

        assertThat(result.getAmount()).isEqualByComparingTo(String.valueOf(expected));
    }

    @Test
    @DisplayName("Should maintain zero balance across conversions")
    void shouldHandleZeroAmount() {
        Money result = exchangeService.convert(Money.zero(), CurrencyType.EUR, CurrencyType.BRL);
        assertThat(result.isZero()).isTrue();
    }
}