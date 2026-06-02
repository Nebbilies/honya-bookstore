package com.honya.bookstore.order.outbox;

import tools.jackson.databind.ObjectMapper;
import com.honya.bookstore.shared.integration.order.OrderEventPublisher;
import com.honya.bookstore.shared.integration.order.event.OrderItemEventDTO;
import com.honya.bookstore.shared.integration.order.event.OrderPlacedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderOutboxRelayTest {

    @Test
    void publishDueMessagesPublishesEventAndMarksSent() {
        OrderOutboxMessageRepository repository = mock(OrderOutboxMessageRepository.class);
        OrderEventPublisher eventPublisher = mock(OrderEventPublisher.class);
        OrderOutboxEventSerializer serializer = new OrderOutboxEventSerializer(new ObjectMapper());
        OrderOutboxMessage message = pendingMessage(serializer);
        when(repository.findDueMessages(any(OffsetDateTime.class), any(Pageable.class))).thenReturn(List.of(message));

        new OrderOutboxRelay(repository, eventPublisher, new OrderOutboxBackoff()).publishDueMessages();

        verify(eventPublisher).publish(eq("ORDER_PLACED"), eq(message.getPayload()));
        assertEquals(OrderOutboxStatus.SENT, message.getStatus());
        assertNotNull(message.getSentAt());
        verify(repository).save(message);
    }

    @Test
    void publishDueMessagesMarksFailedWithBackoffWhenPublisherThrows() {
        OrderOutboxMessageRepository repository = mock(OrderOutboxMessageRepository.class);
        OrderEventPublisher eventPublisher = mock(OrderEventPublisher.class);
        OrderOutboxEventSerializer serializer = new OrderOutboxEventSerializer(new ObjectMapper());
        OrderOutboxMessage message = pendingMessage(serializer);
        when(repository.findDueMessages(any(OffsetDateTime.class), any(Pageable.class))).thenReturn(List.of(message));
        org.mockito.Mockito.doThrow(new RuntimeException("publish failed")).when(eventPublisher).publish(any(), any());

        new OrderOutboxRelay(repository, eventPublisher, new OrderOutboxBackoff()).publishDueMessages();

        assertEquals(OrderOutboxStatus.FAILED, message.getStatus());
        assertEquals(1, message.getAttempts());
        assertTrue(message.getLastError().contains("publish failed"));
        assertTrue(message.getNextAttemptAt().isAfter(OffsetDateTime.now().minusSeconds(1)));
        verify(repository).save(message);
    }

    private OrderOutboxMessage pendingMessage(OrderOutboxEventSerializer serializer) {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        OrderPlacedEvent event = new OrderPlacedEvent(orderId, userId, List.of(new OrderItemEventDTO(UUID.randomUUID(), 2)));
        return OrderOutboxMessage.builder()
                .id(UUID.randomUUID())
                .eventType("ORDER_PLACED")
                .aggregateId(orderId)
                .payload(serializer.serialize(event))
                .status(OrderOutboxStatus.PENDING)
                .attempts(0)
                .nextAttemptAt(OffsetDateTime.now().minusSeconds(1))
                .createdAt(OffsetDateTime.now().minusMinutes(1))
                .updatedAt(OffsetDateTime.now().minusMinutes(1))
                .build();
    }
}
