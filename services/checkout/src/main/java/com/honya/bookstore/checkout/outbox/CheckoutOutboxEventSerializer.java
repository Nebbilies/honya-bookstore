package com.honya.bookstore.checkout.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
class CheckoutOutboxEventSerializer {

    private final ObjectMapper objectMapper;

    String serialize(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to serialize saga command", ex);
        }
    }
}
