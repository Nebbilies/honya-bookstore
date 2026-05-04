package com.honya.bookstore.order.outbox;

import com.honya.bookstore.order.api.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class OrderOutboxWriter {

    private final OrderOutboxMessageRepository repository;
    private final OrderOutboxEventSerializer serializer;

    public void enqueue(OrderPlacedEvent event) {
        OffsetDateTime now = OffsetDateTime.now();
        repository.save(OrderOutboxMessage.builder()
                .eventType("ORDER_PLACED")
                .aggregateId(event.getOrderId())
                .payload(serializer.serialize(event))
                .status(OrderOutboxStatus.PENDING)
                .attempts(0)
                .nextAttemptAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build());
    }
}
