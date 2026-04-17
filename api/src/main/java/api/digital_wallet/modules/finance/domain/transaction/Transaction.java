package api.digital_wallet.modules.finance.domain.transaction;

import api.digital_wallet.modules.finance.domain.enums.CurrencyType;
import api.digital_wallet.modules.finance.domain.transaction.enums.TransactionStatus;
import api.digital_wallet.modules.finance.domain.transaction.enums.TransactionType;
import api.digital_wallet.modules.finance.domain.wallet.Wallet;
import api.digital_wallet.modules.finance.exception.FinanceDomainException;
import api.digital_wallet.modules.finance.exception.FinanceErrorCode;
import api.digital_wallet.shared.domain.BaseEntity;
import api.digital_wallet.shared.value.Money;
import api.digital_wallet.shared.value.converter.MoneyConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Transaction extends BaseEntity {

    @Convert(converter = MoneyConverter.class)
    private Money amount;

    @Enumerated(EnumType.STRING)
    private CurrencyType currency;

    private UUID correlationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    private TransactionType type; // DEBIT ou CREDIT

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    public void authorize() {
        if (this.status != TransactionStatus.PENDING) {
            throw new FinanceDomainException("Only pending transactions can be authorized", FinanceErrorCode.INVALID_STATE_TRANSITION);
        }
        this.status = TransactionStatus.AUTHORIZED;
    }

    public void complete() {
        if (this.status != TransactionStatus.AUTHORIZED && this.status != TransactionStatus.PENDING) {
            throw new FinanceDomainException("Transaction must be authorized or pending to be completed", FinanceErrorCode.INVALID_STATE_TRANSITION);
        }
        this.status = TransactionStatus.COMPLETED;
    }

    public void fail() {
        if (this.isFinalState()) {
            throw new FinanceDomainException("Cannot fail a transaction that is already in a final state", FinanceErrorCode.INVALID_STATE_TRANSITION);
        }
        this.status = TransactionStatus.FAILED;
    }

    public void cancel() {
        if (this.isFinalState()) {
            throw new FinanceDomainException("Cannot cancel a transaction that is already in a final state", FinanceErrorCode.INVALID_STATE_TRANSITION);
        }
        this.status = TransactionStatus.CANCELLED;
    }

    private boolean isFinalState() {
        return this.status == TransactionStatus.COMPLETED ||
                this.status == TransactionStatus.FAILED ||
                this.status == TransactionStatus.CANCELLED;
    }
}
