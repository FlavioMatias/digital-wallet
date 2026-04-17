package api.digital_wallet.modules.finance.infra.dto.response;


import api.digital_wallet.modules.finance.domain.transaction.Transaction;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferResponse(
        UUID transactionId,
        UUID correlationId,
        String status,
        LocalDateTime createdAt
) {
    public static TransferResponse from(Transaction transaction) {
        return new TransferResponse(
                transaction.getId(),
                transaction.getCorrelationId(),
                transaction.getStatus().name(),
                transaction.getCreatedAt()
        );
    }
}
