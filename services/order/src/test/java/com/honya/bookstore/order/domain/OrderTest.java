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

    @SuppressWarnings("unchecked")
    private static List<Object> domainEvents(Order order) {
        return (List<Object>) ReflectionTestUtils.getField(order, "domainEvents");
    }

    private static Order orderWithOneItem(UUID bookId) {
        return Order.builder()
                .items(new ArrayList<>(List.of(OrderItem.builder()
                        .book(OrderItemBook.builder().id(bookId).build())
                        .quantity(2)
                        .price(100)
                        .build())))
                .build();
    }

    @Test
    void placeAssignsIdLinksItemsButDoesNotRegisterEvent() {
        UUID userId = UUID.randomUUID();
        Order order = orderWithOneItem(UUID.randomUUID());

        order.place(userId);

        assertNotNull(order.getId());
        assertEquals(userId, order.getUserId());
        assertSame(order, order.getItems().get(0).getOrder());
        assertEquals(0, domainEvents(order).size());
    }

    @Test
    void confirmRegistersOrderPlacedEvent() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        Order order = orderWithOneItem(bookId);
        order.place(userId);

        order.confirm();

        List<Object> events = domainEvents(order);
        assertEquals(1, events.size());
        OrderPlacedDomainEvent event = (OrderPlacedDomainEvent) events.get(0);
        assertEquals(order.getId(), event.orderId());
        assertEquals(userId, event.userId());
        assertEquals(1, event.lines().size());
        assertEquals(bookId, event.lines().get(0).bookId());
        assertEquals(2, event.lines().get(0).quantity());
    }

    @Test
    void confirmIsIdempotentAndRegistersEventOnlyOnce() {
        Order order = orderWithOneItem(UUID.randomUUID());
        order.place(UUID.randomUUID());

        order.confirm();
        order.confirm();

        assertEquals(1, domainEvents(order).size());
    }

    @Test
    void markPaymentConfirmedRegistersPaymentConfirmedEvent() {
        Order order = orderWithOneItem(UUID.randomUUID());
        order.place(UUID.randomUUID());

        order.markPaymentConfirmed("txn-1");

        List<Object> events = domainEvents(order);
        assertEquals(1, events.size());
        PaymentConfirmedDomainEvent event = (PaymentConfirmedDomainEvent) events.get(0);
        assertEquals(order.getId(), event.orderId());
        assertEquals("txn-1", event.transactionNo());
    }

    @Test
    void markPaymentFailedRegistersPaymentFailedEvent() {
        Order order = orderWithOneItem(UUID.randomUUID());
        order.place(UUID.randomUUID());

        order.markPaymentFailed("VNPAY_24");

        List<Object> events = domainEvents(order);
        assertEquals(1, events.size());
        PaymentFailedDomainEvent event = (PaymentFailedDomainEvent) events.get(0);
        assertEquals(order.getId(), event.orderId());
        assertEquals("VNPAY_24", event.reason());
    }
}
