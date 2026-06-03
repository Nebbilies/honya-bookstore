package com.honya.bookstore.order.application;

import com.honya.bookstore.order.domain.OrderPlacedDomainEvent;
import com.honya.bookstore.order.outbox.OrderOutboxWriter;
import com.honya.bookstore.shared.integration.order.event.OrderPlacedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OrderPlacedDomainEventListenerTest {

    @Test
    void relaysDomainEventToOutboxAsIntegrationEvent() {
        OrderOutboxWriter outboxWriter = mock(OrderOutboxWriter.class);
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        OrderPlacedDomainEvent domainEvent = new OrderPlacedDomainEvent(
                orderId,
                userId,
                List.of(new OrderPlacedDomainEvent.Line(bookId, 2)));

        new OrderPlacedDomainEventListener(outboxWriter).on(domainEvent);

        ArgumentCaptor<OrderPlacedEvent> captor = ArgumentCaptor.forClass(OrderPlacedEvent.class);
        verify(outboxWriter).enqueue(captor.capture());

        OrderPlacedEvent integrationEvent = captor.getValue();
        assertEquals(orderId, integrationEvent.orderId());
        assertEquals(userId, integrationEvent.userId());
        assertEquals(1, integrationEvent.items().size());
        assertEquals(bookId, integrationEvent.items().get(0).bookId());
        assertEquals(2, integrationEvent.items().get(0).quantity());
    }
}
