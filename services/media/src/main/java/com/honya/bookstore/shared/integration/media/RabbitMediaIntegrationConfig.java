package com.honya.bookstore.shared.integration.media;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMediaIntegrationConfig {

    public static final String EXCHANGE = "media.events";
    public static final String DELETED = "media.deleted";

    @Bean
    DirectExchange mediaExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }
}
