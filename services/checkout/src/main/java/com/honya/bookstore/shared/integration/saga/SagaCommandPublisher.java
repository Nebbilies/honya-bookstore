package com.honya.bookstore.shared.integration.saga;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SagaCommandPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(String eventType, String payloadJson) {
        rabbitTemplate.convertAndSend(
                SagaCommandsTopology.EXCHANGE,
                routingKeyFor(eventType),
                payloadJson
        );
    }

    private String routingKeyFor(String eventType) {
        return switch (eventType) {
            case "RELEASE_STOCK" -> SagaCommandsTopology.STOCK_RELEASE;
            case "CANCEL_ORDER" -> SagaCommandsTopology.ORDER_CANCEL;
            default -> throw new IllegalArgumentException("Unsupported saga command type: " + eventType);
        };
    }
}
