package com.honya.bookstore.shared.integration.order;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitOrderIntegrationConfig {

    public static final String CART_QUEUE = "cart.order.events";

    @Bean
    DirectExchange orderExchange() {
        return new DirectExchange(OrderEventsTopology.EXCHANGE, true, false);
    }

    @Bean
    Queue cartOrderEventsQueue() {
        return new Queue(CART_QUEUE, true);
    }

    @Bean
    Binding cartOrderPlacedBinding(Queue cartOrderEventsQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(cartOrderEventsQueue).to(orderExchange).with(OrderEventsTopology.ORDER_PLACED);
    }
}
