package api.digital_wallet.shared.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxDispatcher {

    private final OutboxService outboxService;

    @Scheduled(fixedDelay = 10000) // 10 seconds
    public void dispatch() {
        outboxService.publishPendingEvents();
    }
}