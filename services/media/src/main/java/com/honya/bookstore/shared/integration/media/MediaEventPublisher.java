package com.honya.bookstore.shared.integration.media;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MediaEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(String eventType, String payloadJson) {
        rabbitTemplate.convertAndSend(
                RabbitMediaIntegrationConfig.EXCHANGE,
                routingKeyFor(eventType),
                payloadJson
        );
    }

    private String routingKeyFor(String eventType) {
        return switch (eventType) {
            case "MEDIA_DELETED" -> RabbitMediaIntegrationConfig.DELETED;
            default -> throw new IllegalArgumentException("Unsupported media event type: " + eventType);
        };
    }
}
