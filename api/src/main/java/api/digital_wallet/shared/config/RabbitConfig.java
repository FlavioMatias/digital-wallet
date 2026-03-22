package api.digital_wallet.shared.config;

import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitConfig {

    @Bean
    public MessageConverter messageConverter(JsonMapper jsonMapper) {

        return new JacksonJsonMessageConverter(jsonMapper);
    }
}