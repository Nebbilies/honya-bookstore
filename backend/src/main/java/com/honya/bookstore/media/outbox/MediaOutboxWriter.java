package com.honya.bookstore.media.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MediaOutboxWriter {

    private final MediaOutboxMessageRepository repository;
    private final MediaOutboxEventSerializer serializer;

    public void enqueue(String eventType, UUID aggregateId, Object payload) {
        OffsetDateTime now = OffsetDateTime.now();
        repository.save(MediaOutboxMessage.builder()
                .eventType(eventType)
                .aggregateId(aggregateId)
                .payload(serializer.serialize(payload))
                .status(MediaOutboxStatus.PENDING)
                .attempts(0)
                .nextAttemptAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build());
    }
}
