package com.honya.bookstore.order.outbox;

import tools.jackson.databind.ObjectMapper;
import com.honya.bookstore.order.api.event.OrderItemEventDTO;
import com.honya.bookstore.order.api.event.OrderPlacedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OrderOutboxWriterTest {

    @Test
    void enqueueStoresPendingOrderPlacedEventPayload() throws Exception {
        OrderOutboxMessageRepository repository = mock(OrderOutboxMessageRepository.class);
        OrderOutboxEventSerializer serializer = new OrderOutboxEventSerializer(new ObjectMapper());
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        OrderPlacedEvent event = new OrderPlacedEvent(orderId, userId, List.of(new OrderItemEventDTO(bookId, 2)));

        new OrderOutboxWriter(repository, serializer).enqueue(event);

        ArgumentCaptor<OrderOutboxMessage> messageCaptor = ArgumentCaptor.forClass(OrderOutboxMessage.class);
        verify(repository).save(messageCaptor.capture());
        OrderOutboxMessage message = messageCaptor.getValue();
        assertEquals("ORDER_PLACED", message.getEventType());
        assertEquals(orderId, message.getAggregateId());
        assertEquals(OrderOutboxStatus.PENDING, message.getStatus());
        assertEquals(0, message.getAttempts());
        assertNotNull(message.getNextAttemptAt());
        assertNotNull(message.getCreatedAt());
        assertNotNull(message.getUpdatedAt());
        assertFalse(message.getPayload().isBlank());
        OrderPlacedEvent restored = serializer.deserialize(message.getPayload());
        assertEquals(orderId, restored.getOrderId());
        assertEquals(userId, restored.getUserId());
        assertEquals(bookId, restored.getItems().get(0).getBookId());
        assertEquals(2, restored.getItems().get(0).getQuantity());
    }
}
