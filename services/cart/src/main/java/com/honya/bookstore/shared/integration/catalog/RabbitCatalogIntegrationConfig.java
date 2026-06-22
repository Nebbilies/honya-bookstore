package com.honya.bookstore.shared.integration.catalog;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitCatalogIntegrationConfig {

    public static final String CART_QUEUE = "cart.catalog.events";

    @Bean
    DirectExchange catalogExchange() {
        return new DirectExchange(CatalogEventsTopology.EXCHANGE, true, false);
    }

    @Bean
    Queue cartCatalogEventsQueue() {
        return new Queue(CART_QUEUE, true);
    }

    @Bean
    Binding cartPriceChangedBinding(Queue cartCatalogEventsQueue, DirectExchange catalogExchange) {
        return BindingBuilder.bind(cartCatalogEventsQueue).to(catalogExchange).with(CatalogEventsTopology.PRICE_CHANGED);
    }

    @Bean
    Binding cartDetailsChangedBinding(Queue cartCatalogEventsQueue, DirectExchange catalogExchange) {
        return BindingBuilder.bind(cartCatalogEventsQueue).to(catalogExchange).with(CatalogEventsTopology.DETAILS_CHANGED);
    }

    @Bean
    Binding cartRemovedBinding(Queue cartCatalogEventsQueue, DirectExchange catalogExchange) {
        return BindingBuilder.bind(cartCatalogEventsQueue).to(catalogExchange).with(CatalogEventsTopology.REMOVED);
    }
}
