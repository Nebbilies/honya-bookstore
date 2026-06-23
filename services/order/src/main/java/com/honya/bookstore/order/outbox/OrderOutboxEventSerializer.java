package com.honya.bookstore.order.outbox;

import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class OrderOutboxEventSerializer {

    private final ObjectMapper objectMapper;

    String serialize(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to serialize order integration event", ex);
        }
    }
}
