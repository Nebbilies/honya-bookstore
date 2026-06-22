package com.honya.bookstore.order.domain;

import java.util.List;
import java.util.UUID;

/**
 * Domain event raised by the {@link Order} aggregate when it is placed.
 * Internal to the order module: it is published in-process by Spring Data
 * during {@code repository.save(order)} and handled within the same transaction.
 * An application-layer listener translates it into the cross-module integration
 * event and writes it to the outbox. The aggregate stays unaware of the outbox.
 */
public record OrderPlacedDomainEvent(UUID orderId, UUID userId, List<Line> lines) {

    public record Line(UUID bookId, Integer quantity) {
    }
}
