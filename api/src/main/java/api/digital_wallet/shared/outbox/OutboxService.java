package api.digital_wallet.shared.outbox;

import api.digital_wallet.shared.exceptions.InfraException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxService implements OutboxServicePort {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveEvent(Object event, String aggregateType) {
        try {
            OutboxEvent outbox = new OutboxEvent();

            outbox.setAggregateType(aggregateType);
            outbox.setEventType(event.getClass().getSimpleName());


            String jsonPayload = objectMapper.writeValueAsString(event);
            outbox.setPayload(jsonPayload);

            outboxRepository.save(outbox);

        } catch (JsonProcessingException e) {
            throw new InfraException("Internal Error", "INTERNAL_ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}