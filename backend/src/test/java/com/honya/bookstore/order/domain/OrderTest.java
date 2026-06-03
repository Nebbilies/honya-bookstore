package com.honya.bookstore.order.domain;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class OrderTest {

    @Test
    void placeAssignsIdLinksItemsAndRegistersDomainEvent() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        Order order = Order.builder()
                .items(new ArrayList<>(List.of(OrderItem.builder()
                        .book(OrderItemBook.builder().id(bookId).build())
                        .quantity(2)
                        .price(100)
                        .build())))
                .build();

        order.place(userId);

        assertNotNull(order.getId());
        assertEquals(userId, order.getUserId());
        assertSame(order, order.getItems().get(0).getOrder());

        // AbstractAggregateRoot stores registered events in its private `domainEvents` field
        // (protected accessor lives in Spring's package, so read the field reflectively here).
        @SuppressWarnings("unchecked")
        List<Object> events = (List<Object>) ReflectionTestUtils.getField(order, "domainEvents");
        assertEquals(1, events.size());

        OrderPlacedDomainEvent event = (OrderPlacedDomainEvent) events.get(0);
        assertEquals(order.getId(), event.orderId());
        assertEquals(userId, event.userId());
        assertEquals(1, event.lines().size());
        assertEquals(bookId, event.lines().get(0).bookId());
        assertEquals(2, event.lines().get(0).quantity());
    }
}
