package com.honya.platform.messaging;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class DeadLetterSupportTest {

    @Test
    void topologyDeclaresDurableExchangeQueueAndErrorBinding() {
        Declarables declarables = DeadLetterSupport.deadLetterTopology("catalog.dlx", "catalog.dlq");
        Collection<Declarable> declared = declarables.getDeclarables();

        TopicExchange exchange = single(declared, TopicExchange.class);
        assertThat(exchange.getName()).isEqualTo("catalog.dlx");
        assertThat(exchange.isDurable()).isTrue();

        Queue queue = single(declared, Queue.class);
        assertThat(queue.getName()).isEqualTo("catalog.dlq");
        assertThat(queue.isDurable()).isTrue();

        Binding binding = single(declared, Binding.class);
        assertThat(binding.getExchange()).isEqualTo("catalog.dlx");
        assertThat(binding.getDestination()).isEqualTo("catalog.dlq");
        assertThat(binding.getRoutingKey()).isEqualTo("error.#");
    }

    @SuppressWarnings("unchecked")
    private static <T> T single(Collection<Declarable> declared, Class<T> type) {
        return (T) declared.stream()
                .filter(type::isInstance)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No " + type.getSimpleName() + " declared"));
    }
}
