package com.honya.platform.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

public final class DeadLetterSupport {

    public static final String ROUTING_PATTERN = "error.#";

    private DeadLetterSupport() {
    }

    public static Declarables deadLetterTopology(String exchange, String queue) {
        TopicExchange deadLetterExchange = new TopicExchange(exchange, true, false);
        Queue deadLetterQueue = new Queue(queue, true);
        Binding binding = BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(ROUTING_PATTERN);
        return new Declarables(deadLetterExchange, deadLetterQueue, binding);
    }
}
