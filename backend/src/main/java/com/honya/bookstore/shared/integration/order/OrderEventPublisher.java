package com.honya.bookstore.shared.integration.order;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(String eventType, String payloadJson) {
        rabbitTemplate.convertAndSend(
                RabbitOrderIntegrationConfig.EXCHANGE,
                routingKeyFor(eventType),
                payloadJson
        );
    }

    private String routingKeyFor(String eventType) {
        return switch (eventType) {
            case "ORDER_PLACED" -> RabbitOrderIntegrationConfig.ORDER_PLACED;
            default -> throw new IllegalArgumentException("Unsupported order event type: " + eventType);
        };
    }
}
