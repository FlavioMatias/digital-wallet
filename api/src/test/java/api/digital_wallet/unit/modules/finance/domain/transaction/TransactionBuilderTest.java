package api.digital_wallet.unit.modules.finance.domain.transaction;

import api.digital_wallet.modules.finance.domain.enums.CurrencyType;
import api.digital_wallet.modules.finance.domain.transaction.TransactionBuilder;
import api.digital_wallet.modules.finance.domain.transaction.TransactionPair;
import api.digital_wallet.modules.finance.domain.transaction.enums.TransactionStatus;
import api.digital_wallet.modules.finance.domain.transaction.enums.TransactionType;
import api.digital_wallet.modules.finance.domain.wallet.Wallet;
import api.digital_wallet.modules.finance.exception.FinanceDomainException;
import api.digital_wallet.shared.value.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Unit Test: Transaction Builder")
class TransactionBuilderTest {

    private Wallet origin;
    private Wallet destination;
    private Money amount;
    private Money convertedAmount;

    @BeforeEach
    void setUp() {
        origin = new Wallet();
        ReflectionTestUtils.setField(origin, "id", UUID.randomUUID());
        origin.setCurrency(CurrencyType.BRL);

        destination = new Wallet();
        ReflectionTestUtils.setField(destination, "id", UUID.randomUUID());
        destination.setCurrency(CurrencyType.USD);

        amount = Money.of(100);
        convertedAmount = Money.of(20);
    }

    @Test
    @DisplayName("Should build a valid TransactionPair with linked debit and credit")
    void shouldBuildValidTransactionPair() {
        TransactionBuilder builder = new TransactionBuilder();

        TransactionPair pair = builder
                .from(origin)
                .to(destination)
                .amount(amount)
                .convertedAmount(convertedAmount)
                .build();

        // Assert Debit
        assertThat(pair.debit().getType()).isEqualTo(TransactionType.DEBIT);
        assertThat(pair.debit().getAmount()).isEqualTo(amount);
        assertThat(pair.debit().getWallet()).isEqualTo(origin);
        assertThat(pair.debit().getStatus()).isEqualTo(TransactionStatus.PENDING);

        // Assert Credit
        assertThat(pair.credit().getType()).isEqualTo(TransactionType.CREDIT);
        assertThat(pair.credit().getAmount()).isEqualTo(convertedAmount);
        assertThat(pair.credit().getWallet()).isEqualTo(destination);
        assertThat(pair.credit().getStatus()).isEqualTo(TransactionStatus.PENDING);

        // Assert Correlation
        assertThat(pair.debit().getCorrelationId())
                .isNotNull()
                .isEqualTo(pair.credit().getCorrelationId());
    }

    @Test
    @DisplayName("Should throw exception when origin and destination are the same")
    void shouldFailWhenWalletsAreIdentical() {
        TransactionBuilder builder = new TransactionBuilder();
        UUID sameId = UUID.randomUUID();

        ReflectionTestUtils.setField(origin, "id", sameId);
        ReflectionTestUtils.setField(destination, "id", sameId);

        assertThatThrownBy(() -> builder
                .from(origin)
                .to(destination)
                .amount(amount)
                .build())
                .isInstanceOf(FinanceDomainException.class)
                .hasMessageContaining("Cannot transfer to the same wallet");
    }

    @Test
    @DisplayName("Should throw exception when required fields are missing")
    void shouldFailWhenMissingFields() {
        TransactionBuilder builder = new TransactionBuilder();

        assertThatThrownBy(builder::build)
                .isInstanceOf(FinanceDomainException.class)
                .hasMessageContaining("Origin and destination wallets are required");
    }

    @Test
    @DisplayName("Should throw exception when amount is zero or negative")
    void shouldFailWhenAmountIsInvalid() {
        TransactionBuilder builder = new TransactionBuilder();

        assertThatThrownBy(() -> builder
                .from(origin)
                .to(destination)
                .amount(Money.of(0))
                .build())
                .isInstanceOf(FinanceDomainException.class)
                .hasMessageContaining("Amount must be greater than zero");
    }
}