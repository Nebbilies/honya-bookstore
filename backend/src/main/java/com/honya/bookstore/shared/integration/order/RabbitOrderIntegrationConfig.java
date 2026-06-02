package com.honya.bookstore.shared.integration.order;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitOrderIntegrationConfig {

    public static final String EXCHANGE = "order.events";
    public static final String CART_QUEUE = "cart.order.events";
    public static final String CATALOG_QUEUE = "catalog.order.events";
    public static final String ORDER_PLACED = "order.placed";

    @Bean
    DirectExchange orderExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue cartOrderEventsQueue() {
        return new Queue(CART_QUEUE, true);
    }

    @Bean
    Queue catalogOrderEventsQueue() {
        return new Queue(CATALOG_QUEUE, true);
    }

    @Bean
    Binding cartOrderPlacedBinding(Queue cartOrderEventsQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(cartOrderEventsQueue).to(orderExchange).with(ORDER_PLACED);
    }

    @Bean
    Binding catalogOrderPlacedBinding(Queue catalogOrderEventsQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(catalogOrderEventsQueue).to(orderExchange).with(ORDER_PLACED);
    }
}
