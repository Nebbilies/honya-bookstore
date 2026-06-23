package com.honya.bookstore.order.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderOutboxWriter {

    private final OrderOutboxMessageRepository repository;
    private final OrderOutboxEventSerializer serializer;

    public void enqueue(String eventType, UUID aggregateId, Object event) {
        OffsetDateTime now = OffsetDateTime.now();
        repository.save(OrderOutboxMessage.builder()
                .eventType(eventType)
                .aggregateId(aggregateId)
                .payload(serializer.serialize(event))
                .status(OrderOutboxStatus.PENDING)
                .attempts(0)
                .nextAttemptAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build());
    }
}
