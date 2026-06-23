package com.honya.bookstore.checkout.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CheckoutOutboxWriter {

    private final CheckoutOutboxMessageRepository repository;
    private final CheckoutOutboxEventSerializer serializer;

    public void enqueue(String eventType, UUID aggregateId, Object event) {
        OffsetDateTime now = OffsetDateTime.now();
        repository.save(CheckoutOutboxMessage.builder()
                .eventType(eventType)
                .aggregateId(aggregateId)
                .payload(serializer.serialize(event))
                .status(CheckoutOutboxStatus.PENDING)
                .attempts(0)
                .nextAttemptAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build());
    }
}
