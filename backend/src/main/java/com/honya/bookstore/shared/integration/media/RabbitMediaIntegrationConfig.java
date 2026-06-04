package com.honya.bookstore.shared.integration.media;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMediaIntegrationConfig {

    public static final String EXCHANGE = "media.events";
    public static final String CATALOG_QUEUE = "catalog.media.events";
    public static final String DELETED = "media.deleted";

    @Bean
    DirectExchange mediaExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue catalogMediaEventsQueue() {
        return new Queue(CATALOG_QUEUE, true);
    }

    @Bean
    Binding mediaDeletedBinding(Queue catalogMediaEventsQueue, DirectExchange mediaExchange) {
        return BindingBuilder.bind(catalogMediaEventsQueue).to(mediaExchange).with(DELETED);
    }
}
