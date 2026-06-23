package com.honya.bookstore.shared.integration.saga;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitSagaCommandConfig {

    @Bean
    DirectExchange sagaCommandsExchange() {
        return new DirectExchange(SagaCommandsTopology.EXCHANGE, true, false);
    }
}
