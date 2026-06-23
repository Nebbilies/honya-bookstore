package com.honya.bookstore.shared.integration.saga;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitSagaCommandConfig {

    public static final String ORDER_QUEUE = "order.saga.commands";

    @Bean
    DirectExchange sagaCommandsExchange() {
        return new DirectExchange(SagaCommandsTopology.EXCHANGE, true, false);
    }

    @Bean
    Queue orderSagaCommandsQueue() {
        return new Queue(ORDER_QUEUE, true);
    }

    @Bean
    Binding orderCancelBinding(Queue orderSagaCommandsQueue, DirectExchange sagaCommandsExchange) {
        return BindingBuilder.bind(orderSagaCommandsQueue).to(sagaCommandsExchange).with(SagaCommandsTopology.ORDER_CANCEL);
    }
}
