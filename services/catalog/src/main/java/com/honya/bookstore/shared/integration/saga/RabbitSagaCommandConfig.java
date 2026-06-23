package com.honya.bookstore.shared.integration.saga;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitSagaCommandConfig {

    public static final String CATALOG_QUEUE = "catalog.saga.commands";

    @Bean
    DirectExchange sagaCommandsExchange() {
        return new DirectExchange(SagaCommandsTopology.EXCHANGE, true, false);
    }

    @Bean
    Queue catalogSagaCommandsQueue() {
        return new Queue(CATALOG_QUEUE, true);
    }

    @Bean
    Binding stockReleaseBinding(Queue catalogSagaCommandsQueue, DirectExchange sagaCommandsExchange) {
        return BindingBuilder.bind(catalogSagaCommandsQueue).to(sagaCommandsExchange).with(SagaCommandsTopology.STOCK_RELEASE);
    }
}
