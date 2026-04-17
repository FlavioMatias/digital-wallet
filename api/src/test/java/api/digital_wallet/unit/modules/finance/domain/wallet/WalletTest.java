package api.digital_wallet.unit.modules.finance.domain.wallet;

import api.digital_wallet.modules.finance.domain.enums.CurrencyType;
import api.digital_wallet.modules.finance.domain.wallet.Wallet;
import api.digital_wallet.modules.finance.domain.wallet.enums.WalletStatus;
import api.digital_wallet.modules.finance.exception.FinanceDomainException;
import api.digital_wallet.modules.finance.exception.FinanceErrorCode;
import api.digital_wallet.shared.value.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Unit Test: Wallet Domain Entity")
class WalletTest {

    private Wallet wallet;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        wallet.setOwnerId(UUID.randomUUID());
        wallet.setCurrency(CurrencyType.BRL);
        wallet.setStatus(WalletStatus.ACTIVE);
        wallet.setAvailableBalance(Money.of(100));
        wallet.setBlockedBalance(Money.zero());
    }

    @Nested
    @DisplayName("Deposit Scenarios")
    class DepositScenarios {
        @Test
        @DisplayName("Should increase available balance when depositing")
        void shouldIncreaseBalanceOnDeposit() {
            wallet.deposit(Money.of(50));
            assertThat(wallet.getAvailableBalance().getAmount()).isEqualByComparingTo("150");
        }

        @Test
        @DisplayName("Should fail deposit if wallet is not active")
        void shouldFailDepositWhenInactive() {
            wallet.setStatus(WalletStatus.BLOCKED);
            assertThatThrownBy(() -> wallet.deposit(Money.of(10)))
                    .isInstanceOf(FinanceDomainException.class)
                    .hasFieldOrPropertyWithValue("errorCode", FinanceErrorCode.WALLET_BLOCKED);
        }

        @Test
        @DisplayName("Should fail deposit if amount is zero or negative")
        void shouldFailDepositWhenAmountIsInvalid() {
            assertThatThrownBy(() -> wallet.deposit(Money.of(0)))
                    .isInstanceOf(FinanceDomainException.class);

            assertThatThrownBy(() -> wallet.deposit(Money.of(-10)))
                    .isInstanceOf(FinanceDomainException.class);
        }
    }

    @Nested
    @DisplayName("Reservation and Confirmation Scenarios")
    class ReservationScenarios {
        @Test
        @DisplayName("Should move funds from available to blocked when reserving")
        void shouldMoveFundsToBlockedOnReserve() {
            wallet.reserveBalance(Money.of(30));

            assertThat(wallet.getAvailableBalance().getAmount()).isEqualByComparingTo("70");
            assertThat(wallet.getBlockedBalance().getAmount()).isEqualByComparingTo("30");
        }

        @Test
        @DisplayName("Should remove from blocked when confirming payment")
        void shouldRemoveFromBlockedOnConfirm() {
            wallet.reserveBalance(Money.of(30));
            wallet.confirmPaymentFromBlocked(Money.of(30));

            assertThat(wallet.getBlockedBalance().isZero()).isTrue();
            assertThat(wallet.getAvailableBalance().getAmount()).isEqualByComparingTo("70");
        }

        @Test
        @DisplayName("Should throw error when reserving more than available balance")
        void shouldFailIfInsufficientFunds() {
            assertThatThrownBy(() -> wallet.reserveBalance(Money.of(200)))
                    .isInstanceOf(FinanceDomainException.class)
                    .hasFieldOrPropertyWithValue("errorCode", FinanceErrorCode.INSUFFICIENT_FUNDS);
        }
    }

    @Nested
    @DisplayName("Rollback Scenarios")
    class RollbackScenarios {
        @Test
        @DisplayName("Should return blocked funds to available balance on rollback")
        void shouldReturnFundsOnRollback() {
            wallet.reserveBalance(Money.of(50));
            wallet.rollbackBlockedBalance(Money.of(50));

            assertThat(wallet.getAvailableBalance().getAmount()).isEqualByComparingTo("100");
            assertThat(wallet.getBlockedBalance().isZero()).isTrue();
        }

        @Test
        @DisplayName("Should fail rollback if amount exceeds current blocked balance")
        void shouldFailRollbackIfAmountExceedsBlocked() {
            wallet.reserveBalance(Money.of(10));
            assertThatThrownBy(() -> wallet.rollbackBlockedBalance(Money.of(20)))
                    .isInstanceOf(FinanceDomainException.class);
        }
    }
}