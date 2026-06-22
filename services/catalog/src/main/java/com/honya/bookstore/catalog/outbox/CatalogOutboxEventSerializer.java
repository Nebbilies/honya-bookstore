package com.honya.bookstore.catalog.outbox;

import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class CatalogOutboxEventSerializer {

    private final ObjectMapper objectMapper;

    String serialize(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to serialize catalog outbox payload", ex);
        }
    }
}
