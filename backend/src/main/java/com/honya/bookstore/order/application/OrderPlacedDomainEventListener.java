package com.honya.bookstore.order.application;

import com.honya.bookstore.order.domain.OrderPlacedDomainEvent;
import com.honya.bookstore.order.outbox.OrderOutboxWriter;
import com.honya.bookstore.shared.integration.order.event.OrderItemEventDTO;
import com.honya.bookstore.shared.integration.order.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Bridges the in-process OrderPlacedDomainEvent (raised by the Order
 * aggregate) to the cross-module integration OrderPlacedEvent on the
 * outbox.
 */
@Component
@RequiredArgsConstructor
class OrderPlacedDomainEventListener {

    private final OrderOutboxWriter outboxWriter;

    @EventListener
    void on(OrderPlacedDomainEvent event) {
        List<OrderItemEventDTO> items = event.lines().stream()
                .map(line -> new OrderItemEventDTO(line.bookId(), line.quantity()))
                .toList();

        outboxWriter.enqueue(new OrderPlacedEvent(event.orderId(), event.userId(), items));
    }
}
