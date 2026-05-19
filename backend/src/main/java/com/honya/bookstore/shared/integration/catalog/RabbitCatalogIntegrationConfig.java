package com.honya.bookstore.shared.integration.catalog;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitCatalogIntegrationConfig {

    public static final String EXCHANGE = "catalog.events";
    public static final String CART_QUEUE = "cart.catalog.events";
    public static final String PRICE_CHANGED = "product.price.changed";
    public static final String DETAILS_CHANGED = "product.details.changed";
    public static final String REMOVED = "product.removed";

    @Bean
    DirectExchange catalogExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue cartCatalogEventsQueue() {
        return new Queue(CART_QUEUE, true);
    }

    @Bean
    Binding priceChangedBinding(Queue cartCatalogEventsQueue, DirectExchange catalogExchange) {
        return BindingBuilder.bind(cartCatalogEventsQueue).to(catalogExchange).with(PRICE_CHANGED);
    }

    @Bean
    Binding detailsChangedBinding(Queue cartCatalogEventsQueue, DirectExchange catalogExchange) {
        return BindingBuilder.bind(cartCatalogEventsQueue).to(catalogExchange).with(DETAILS_CHANGED);
    }

    @Bean
    Binding removedBinding(Queue cartCatalogEventsQueue, DirectExchange catalogExchange) {
        return BindingBuilder.bind(cartCatalogEventsQueue).to(catalogExchange).with(REMOVED);
    }
}
