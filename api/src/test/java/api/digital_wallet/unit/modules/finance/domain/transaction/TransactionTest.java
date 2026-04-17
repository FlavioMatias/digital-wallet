package api.digital_wallet.unit.modules.finance.domain.transaction;

import api.digital_wallet.modules.finance.domain.transaction.Transaction;
import api.digital_wallet.modules.finance.domain.transaction.enums.TransactionStatus;
import api.digital_wallet.modules.finance.exception.FinanceDomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Unit Test: Transaction Domain Entity")
class TransactionTest {

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = new Transaction();
        transaction.setStatus(TransactionStatus.PENDING);
    }

    @Test
    @DisplayName("Deve autorizar uma transação pendente com sucesso")
    void shouldAuthorizePendingTransaction() {
        transaction.authorize();
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.AUTHORIZED);
    }

    @Test
    @DisplayName("Deve completar uma transação autorizada com sucesso")
    void shouldCompleteAuthorizedTransaction() {
        transaction.setStatus(TransactionStatus.AUTHORIZED);
        transaction.complete();
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
    }

    @Test
    @DisplayName("Deve falhar ao tentar autorizar uma transação que já está completa")
    void shouldThrowExceptionWhenAuthorizingCompletedTransaction() {
        transaction.setStatus(TransactionStatus.COMPLETED);

        assertThatThrownBy(() -> transaction.authorize())
                .isInstanceOf(FinanceDomainException.class)
                .hasMessageContaining("Only pending transactions can be authorized");
    }

    @ParameterizedTest
    @EnumSource(value = TransactionStatus.class, names = {"COMPLETED", "FAILED", "CANCELLED"})
    @DisplayName("Não deve permitir cancelar transações em estados finais")
    void shouldNotAllowCancelInFinalStates(TransactionStatus finalStatus) {
        transaction.setStatus(finalStatus);

        assertThatThrownBy(() -> transaction.cancel())
                .isInstanceOf(FinanceDomainException.class)
                .hasMessageContaining("Cannot cancel a transaction");
    }

    @Test
    @DisplayName("Deve transitar para FAILED com sucesso a partir de PENDING")
    void shouldTransitionToFailed() {
        transaction.fail();
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.FAILED);
    }
}