package com.honya.bookstore.shared.integration.order;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitOrderIntegrationConfig {

    @Bean
    DirectExchange orderExchange() {
        return new DirectExchange(OrderEventsTopology.EXCHANGE, true, false);
    }
}
