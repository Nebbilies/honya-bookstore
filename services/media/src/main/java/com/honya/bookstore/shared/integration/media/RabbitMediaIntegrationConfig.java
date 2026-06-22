package com.honya.bookstore.shared.integration.media;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMediaIntegrationConfig {

    @Bean
    DirectExchange mediaExchange() {
        return new DirectExchange(MediaEventsTopology.EXCHANGE, true, false);
    }
}
