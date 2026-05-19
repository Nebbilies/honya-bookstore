package com.honya.bookstore.catalog.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CatalogOutboxWriter {

    private final CatalogOutboxMessageRepository repository;
    private final CatalogOutboxEventSerializer serializer;

    public void enqueue(String eventType, UUID aggregateId, Object payload) {
        OffsetDateTime now = OffsetDateTime.now();
        repository.save(CatalogOutboxMessage.builder()
                .eventType(eventType)
                .aggregateId(aggregateId)
                .payload(serializer.serialize(payload))
                .status(CatalogOutboxStatus.PENDING)
                .attempts(0)
                .nextAttemptAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build());
    }
}
