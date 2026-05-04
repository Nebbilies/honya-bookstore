package com.honya.bookstore.order.outbox;

import tools.jackson.databind.ObjectMapper;
import com.honya.bookstore.order.api.event.OrderItemEventDTO;
import com.honya.bookstore.order.api.event.OrderPlacedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderOutboxRelayTest {

    @Test
    void publishDueMessagesPublishesEventAndMarksSent() throws Exception {
        OrderOutboxMessageRepository repository = mock(OrderOutboxMessageRepository.class);
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        OrderOutboxEventSerializer serializer = new OrderOutboxEventSerializer(new ObjectMapper());
        OrderOutboxMessage message = pendingMessage(serializer);
        when(repository.findDueMessages(any(OffsetDateTime.class), any(Pageable.class))).thenReturn(List.of(message));

        new OrderOutboxRelay(repository, eventPublisher, serializer, new OrderOutboxBackoff()).publishDueMessages();

        ArgumentCaptor<OrderPlacedEvent> eventCaptor = ArgumentCaptor.forClass(OrderPlacedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(message.getAggregateId(), eventCaptor.getValue().getOrderId());
        assertEquals(OrderOutboxStatus.SENT, message.getStatus());
        assertNotNull(message.getSentAt());
        verify(repository).save(message);
    }

    @Test
    void publishDueMessagesMarksFailedWithBackoffWhenPublisherThrows() throws Exception {
        OrderOutboxMessageRepository repository = mock(OrderOutboxMessageRepository.class);
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        OrderOutboxEventSerializer serializer = new OrderOutboxEventSerializer(new ObjectMapper());
        OrderOutboxMessage message = pendingMessage(serializer);
        when(repository.findDueMessages(any(OffsetDateTime.class), any(Pageable.class))).thenReturn(List.of(message));
        org.mockito.Mockito.doThrow(new RuntimeException("listener failed")).when(eventPublisher).publishEvent(any(OrderPlacedEvent.class));

        new OrderOutboxRelay(repository, eventPublisher, serializer, new OrderOutboxBackoff()).publishDueMessages();

        assertEquals(OrderOutboxStatus.FAILED, message.getStatus());
        assertEquals(1, message.getAttempts());
        assertTrue(message.getLastError().contains("listener failed"));
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
