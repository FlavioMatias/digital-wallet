package api.digital_wallet.modules.finance.infra.mq.event;
import java.util.UUID;

public record UserCreatedEvent(
        UUID userId,
        String email,
        String baseCurrency
) {}