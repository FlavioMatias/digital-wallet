package api.digital_wallet.unit.modules.finance.domain.service;

import api.digital_wallet.modules.finance.domain.enums.CurrencyType;
import api.digital_wallet.modules.finance.domain.service.TransferService;
import api.digital_wallet.modules.finance.domain.transaction.Transaction;
import api.digital_wallet.modules.finance.domain.transaction.TransactionPair;
import api.digital_wallet.modules.finance.domain.transaction.enums.TransactionStatus;
import api.digital_wallet.modules.finance.domain.wallet.Wallet;
import api.digital_wallet.modules.finance.domain.wallet.enums.WalletStatus;
import api.digital_wallet.modules.finance.exception.FinanceDomainException;
import api.digital_wallet.modules.finance.exception.FinanceErrorCode;
import api.digital_wallet.shared.value.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Unit Test: Transfer Domain Service")
class TransferServiceTest {

    private TransferService transferService;
    private Wallet origin;
    private Wallet destination;

    @BeforeEach
    void setUp() {
        transferService = new TransferService();

        origin = new Wallet();
        origin.setStatus(WalletStatus.ACTIVE);
        origin.setAvailableBalance(Money.of(100));
        origin.setBlockedBalance(Money.zero());
        origin.setCurrency(CurrencyType.BRL);

        destination = new Wallet();
        destination.setStatus(WalletStatus.ACTIVE);
        destination.setAvailableBalance(Money.of(50));
        destination.setBlockedBalance(Money.zero());
        destination.setCurrency(CurrencyType.BRL);
    }

    @Test
    @DisplayName("Should execute transfer successfully when transactions are authorized")
    void shouldExecuteTransferSuccessfully() {
        // Arrange
        Money amount = Money.of(40);
        Transaction debit = new Transaction();
        debit.setAmount(amount);
        debit.setStatus(TransactionStatus.AUTHORIZED);

        Transaction credit = new Transaction();
        credit.setAmount(amount);
        credit.setStatus(TransactionStatus.AUTHORIZED);

        TransactionPair pair = new TransactionPair(debit, credit);

        // Act
        transferService.execute(origin, destination, pair);

        // Assert
        assertThat(origin.getAvailableBalance().getAmount()).isEqualByComparingTo("60.00");
        assertThat(origin.getBlockedBalance().isZero()).isTrue();

        assertThat(destination.getAvailableBalance().getAmount()).isEqualByComparingTo("90.00");

        assertThat(debit.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(credit.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should throw exception when transactions are not authorized")
    void shouldFailWhenTransactionsNotAuthorized() {
        // Arrange
        Transaction debit = new Transaction();
        debit.setStatus(TransactionStatus.PENDING);
        Transaction credit = new Transaction();
        credit.setStatus(TransactionStatus.PENDING);
        TransactionPair pair = new TransactionPair(debit, credit);

        // Act & Assert
        assertThatThrownBy(() -> transferService.execute(origin, destination, pair))
                .isInstanceOf(FinanceDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", FinanceErrorCode.UNAUTHORIZED_TRANSFER)
                .hasMessageContaining("It is not possible to complete transfers with unauthorized transactions");

        // Ensure balances remained untouched
        assertThat(origin.getAvailableBalance().getAmount()).isEqualByComparingTo("100.00");
        assertThat(destination.getAvailableBalance().getAmount()).isEqualByComparingTo("50.00");
    }
}