package api.digital_wallet.shared.outbox;

import api.digital_wallet.shared.exceptions.InfraException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService implements OutboxServicePort {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

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
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxRepository.findByProcessedAtIsNull();

        for (OutboxEvent event : events) {
            try {
                Message message = MessageBuilder
                        .withBody(event.getPayload().getBytes(StandardCharsets.UTF_8))
                        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                        .build();
                rabbitTemplate.send(event.getAggregateType(), event.getEventType(), message);

                event.markAsProcessed();
                outboxRepository.save(event);

                log.info("Evento outbox {} enviado e marcado como processado", event.getId());
            } catch (Exception e) {
                log.error("Erro ao enviar evento outbox {}: {}", event.getId(), e.getMessage());
            }
        }
    }
}