package com.honya.bookstore.media.outbox;

import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MediaOutboxEventSerializer {

    private final ObjectMapper objectMapper;

    String serialize(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to serialize media outbox payload", ex);
        }
    }
}
