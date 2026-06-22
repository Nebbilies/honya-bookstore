package com.honya.bookstore.shared.integration.catalog;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CatalogEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(String eventType, String payloadJson) {
        rabbitTemplate.convertAndSend(
                CatalogEventsTopology.EXCHANGE,
                routingKeyFor(eventType),
                payloadJson
        );
    }

    private String routingKeyFor(String eventType) {
        return switch (eventType) {
            case "PRODUCT_PRICE_CHANGED" -> CatalogEventsTopology.PRICE_CHANGED;
            case "PRODUCT_DETAILS_CHANGED" -> CatalogEventsTopology.DETAILS_CHANGED;
            case "PRODUCT_REMOVED" -> CatalogEventsTopology.REMOVED;
            default -> throw new IllegalArgumentException("Unsupported catalog event type: " + eventType);
        };
    }
}
