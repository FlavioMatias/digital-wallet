package api.digital_wallet.modules.finance.domain.transaction.event;

import java.math.BigDecimal;
import java.util.UUID;


public record TransactionCreatedEvent(
        UUID correlationId,
        UUID originId,
        UUID destinationId,
        BigDecimal amount,
        String currency
) {}