package api.digital_wallet.shared.outbox;

public interface OutboxServicePort {
    void saveEvent(Object event, String aggregateType);
}
