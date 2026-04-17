package api.digital_wallet.unit.modules.finance.domain.transaction;

import api.digital_wallet.modules.finance.domain.transaction.Transaction;
import api.digital_wallet.modules.finance.domain.transaction.TransactionPair;
import api.digital_wallet.modules.finance.domain.transaction.enums.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Unit Test: Transaction Pair")
class TransactionPairTest {

    private Transaction debit;
    private Transaction credit;
    private TransactionPair pair;

    @BeforeEach
    void setUp() {
        debit = new Transaction();
        debit.setStatus(TransactionStatus.PENDING);

        credit = new Transaction();
        credit.setStatus(TransactionStatus.PENDING);

        pair = new TransactionPair(debit, credit);
    }

    @Test
    @DisplayName("Should throw exception if either transaction is null")
    void shouldThrowExceptionWhenNullTransactions() {
        assertThatThrownBy(() -> new TransactionPair(null, credit))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new TransactionPair(debit, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should authorize both transactions and return true for isAuthorized")
    void shouldAuthorizeBothTransactions() {
        pair.authorize();

        assertThat(debit.getStatus()).isEqualTo(TransactionStatus.AUTHORIZED);
        assertThat(credit.getStatus()).isEqualTo(TransactionStatus.AUTHORIZED);
        assertThat(pair.isAuthorized()).isTrue();
    }

    @Test
    @DisplayName("Should complete both transactions")
    void shouldCompleteBothTransactions() {
        debit.setStatus(TransactionStatus.AUTHORIZED);
        credit.setStatus(TransactionStatus.AUTHORIZED);

        pair.complet();

        assertThat(debit.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(credit.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should fail both transactions")
    void shouldFailBothTransactions() {
        pair.fail();

        assertThat(debit.getStatus()).isEqualTo(TransactionStatus.FAILED);
        assertThat(credit.getStatus()).isEqualTo(TransactionStatus.FAILED);
    }

    @Test
    @DisplayName("Should return false for isAuthorized if only one is authorized")
    void shouldReturnFalseIfOnlyOneIsAuthorized() {
        debit.setStatus(TransactionStatus.AUTHORIZED);
        credit.setStatus(TransactionStatus.PENDING);

        assertThat(pair.isAuthorized()).isFalse();
    }

    @Test
    @DisplayName("Should convert pair to list containing both transactions")
    void shouldConvertToList() {
        var list = pair.toList();

        assertThat(list).hasSize(2);
        assertThat(list).containsExactly(debit, credit);
    }
}