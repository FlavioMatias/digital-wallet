package api.digital_wallet.modules.finance.domain.wallet;

import api.digital_wallet.modules.finance.domain.enums.CurrencyType;
import api.digital_wallet.modules.finance.domain.wallet.enums.WalletStatus;
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
@Table(name = "wallets")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Wallet extends BaseEntity {

    @Convert(converter = MoneyConverter.class)
    @Column(name = "available_balance", nullable = false)
    private Money availableBalance = Money.zero();

    @Convert(converter = MoneyConverter.class)
    @Column(name = "blocked_balance", nullable = false)
    private Money blockedBalance = Money.zero();

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private CurrencyType currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WalletStatus status;

    @Column(name = "owner_id")
    private UUID owner;

    public void deposit(Money amount) {
        if (this.status != WalletStatus.ACTIVE) {
            throw new FinanceDomainException("Cannot deposit: wallet is not active", FinanceErrorCode.WALLET_BLOCKED);
        }

        if (amount == null || amount.isLessThanOrEqualToZero()) {
            throw new FinanceDomainException("Deposit amount must be greater than zero", FinanceErrorCode.DOMAIN_ERROR);
        }

        this.availableBalance = this.availableBalance.add(amount);
    }

    public void reserveBalance(Money amount) {
        if (amount == null || amount.isLessThanOrEqualToZero()) {
            throw new FinanceDomainException("Reserve amount must be greater than zero", FinanceErrorCode.DOMAIN_ERROR);
        }

        if (this.availableBalance.isLessThan(amount)) {
            throw new FinanceDomainException("Insufficient available balance for reservation", FinanceErrorCode.INSUFFICIENT_FUNDS);
        }

        this.availableBalance = this.availableBalance.subtract(amount);
        this.blockedBalance = this.blockedBalance.add(amount);
    }

    public void confirmPaymentFromBlocked(Money amount) {
        if (amount == null || amount.isLessThanOrEqualToZero()) {
            throw new FinanceDomainException("Payment amount must be greater than zero", FinanceErrorCode.DOMAIN_ERROR);
        }

        if (this.blockedBalance.isLessThan(amount)) {
            throw new FinanceDomainException("Insufficient blocked balance to confirm this payment", FinanceErrorCode.INSUFFICIENT_FUNDS);
        }

        this.blockedBalance = this.blockedBalance.subtract(amount);
    }

    public void rollbackBlockedBalance(Money amount) {
        if (amount == null || amount.isLessThanOrEqualToZero()) {
            throw new FinanceDomainException("Rollback amount must be greater than zero", FinanceErrorCode.DOMAIN_ERROR);
        }

        if (this.blockedBalance.isLessThan(amount)) {
            throw new FinanceDomainException("Insufficient blocked balance to perform rollback", FinanceErrorCode.INSUFFICIENT_FUNDS);
        }

        this.blockedBalance = this.blockedBalance.subtract(amount);
        this.availableBalance = this.availableBalance.add(amount);
    }
}