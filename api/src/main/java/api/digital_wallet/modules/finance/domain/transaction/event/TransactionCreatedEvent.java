package api.digital_wallet.modules.finance.domain.transaction.event;

import api.digital_wallet.modules.finance.domain.enums.CurrencyType;
import api.digital_wallet.modules.finance.domain.transaction.TransactionPair;
import api.digital_wallet.shared.value.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


public record TransactionCreatedEvent(
        UUID correlationId,
        UUID originId,
        UUID destinationId,
        Money amount,
        CurrencyType currency,
        LocalDateTime occurredAt
) {

    public static TransactionCreatedEvent from(TransactionPair pair) {
        var debit = pair.debit();
        var credit = pair.credit();

        return new TransactionCreatedEvent(
                debit.getCorrelationId(),
                debit.getWallet().getOwnerId(),
                credit.getWallet().getOwnerId(),
                credit.getAmount(),
                credit.getCurrency(),
                LocalDateTime.now()
        );
    }
}