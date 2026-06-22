package com.honya.bookstore.shared.integration.catalog;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitCatalogIntegrationConfig {

    @Bean
    DirectExchange catalogExchange() {
        return new DirectExchange(CatalogEventsTopology.EXCHANGE, true, false);
    }
}
