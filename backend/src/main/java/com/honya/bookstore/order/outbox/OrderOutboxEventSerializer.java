package com.honya.bookstore.order.outbox;

import tools.jackson.databind.ObjectMapper;
import com.honya.bookstore.shared.integration.order.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class OrderOutboxEventSerializer {

    private final ObjectMapper objectMapper;

    String serialize(OrderPlacedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to serialize OrderPlacedEvent", ex);
        }
    }
}
