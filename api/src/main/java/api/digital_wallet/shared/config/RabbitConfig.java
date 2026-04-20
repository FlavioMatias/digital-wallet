package api.digital_wallet.shared.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {


    public static final String USER_CREATED_QUEUE = "user.created.wallet-service";
    public static final String USER_EXCHANGE = "user-events";
    public static final String USER_CREATED_ROUTING_KEY = "UserCreatedEvent";

    // DLQ (Dead Letter Queue)
    public static final String WALLET_DLX = "wallet-service.dlx";
    public static final String USER_CREATED_DLQ = "user.created.wallet-service.dlq";
    public static final String DLQ_ROUTING_KEY = "dead-letter";

    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(WALLET_DLX);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(USER_CREATED_DLQ, true);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(DLQ_ROUTING_KEY);
    }

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    @Bean
    public Queue userCreatedQueue() {
        return QueueBuilder.durable(USER_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", WALLET_DLX)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding userCreatedBinding(Queue userCreatedQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userCreatedQueue)
                .to(userExchange)
                .with(USER_CREATED_ROUTING_KEY);
    }
}