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
                OrderEventsTopology.EXCHANGE,
                routingKeyFor(eventType),
                payloadJson
        );
    }

    private String routingKeyFor(String eventType) {
        return switch (eventType) {
            case "ORDER_PLACED" -> OrderEventsTopology.ORDER_PLACED;
            case "PAYMENT_CONFIRMED" -> OrderEventsTopology.PAYMENT_CONFIRMED;
            case "PAYMENT_FAILED" -> OrderEventsTopology.PAYMENT_FAILED;
            case "PAYMENT_RETRIED" -> OrderEventsTopology.PAYMENT_RETRIED;
            default -> throw new IllegalArgumentException("Unsupported order event type: " + eventType);
        };
    }
}
