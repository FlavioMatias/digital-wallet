package api.digital_wallet.modules.finance.domain.transaction;

import api.digital_wallet.modules.finance.domain.service.ExchangeService;
import api.digital_wallet.modules.finance.domain.transaction.enums.TransactionStatus;
import api.digital_wallet.modules.finance.domain.transaction.enums.TransactionType;
import api.digital_wallet.modules.finance.domain.wallet.Wallet;
import api.digital_wallet.modules.finance.exception.FinanceDomainException;
import api.digital_wallet.modules.finance.exception.FinanceErrorCode;
import api.digital_wallet.shared.value.Money;

import java.util.UUID;

public class TransactionBuilder {

    private final ExchangeService exchangeService;
    private Wallet origin;
    private Wallet destination;
    private Money amount;

    public TransactionBuilder(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    public TransactionBuilder from(Wallet origin) {
        this.origin = origin;
        return this;
    }

    public TransactionBuilder to(Wallet destination) {
        this.destination = destination;
        return this;
    }

    public TransactionBuilder amount(Money amount) {
        this.amount = amount;
        return this;
    }

    public TransactionPair build() {
        validate();

        UUID correlationId = UUID.randomUUID();

        Transaction debit = new Transaction(
                amount,
                origin.getCurrency(),
                correlationId,
                origin,
                TransactionType.DEBIT,
                TransactionStatus.PENDING
        );

        // Cálculo do Câmbio
        Money convertedAmount = exchangeService.convert(
                amount,
                origin.getCurrency(),
                destination.getCurrency()
        );


        Transaction credit = new Transaction(
                convertedAmount,
                destination.getCurrency(),
                correlationId,
                destination,
                TransactionType.CREDIT,
                TransactionStatus.PENDING
        );

        return new TransactionPair(debit, credit);
    }

    private void validate() {
        if (origin == null || destination == null) {
            throw new FinanceDomainException("Origin and destination wallets are required", FinanceErrorCode.DOMAIN_ERROR);
        }
        if (amount == null || amount.isLessThanOrEqualToZero()) {
            throw new FinanceDomainException("Amount must be greater than zero", FinanceErrorCode.DOMAIN_ERROR);
        }
        if (origin.getId().equals(destination.getId())) {
            throw new FinanceDomainException("Cannot transfer to the same wallet", FinanceErrorCode.DOMAIN_ERROR);
        }
    }
}