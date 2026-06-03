package com.honya.bookstore.order.application;

import com.honya.bookstore.order.domain.Order;
import com.honya.bookstore.order.domain.OrderItem;
import com.honya.bookstore.order.domain.OrderItemBook;
import com.honya.bookstore.order.domain.OrderStatus;
import com.honya.bookstore.order.infrastructure.persistence.OrderRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderServiceImplTest {

    @Test
    void createOrderPlacesOrderAndPersists() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        Order orderDetails = Order.builder()
                .firstName("Ada")
                .lastName("Lovelace")
                .address("12 Example Street")
                .city("London")
                .items(List.of(OrderItem.builder()
                        .book(OrderItemBook.builder().id(bookId).build())
                        .quantity(2)
                        .price(100)
                        .build()))
                .build();

        Order createdOrder = new OrderServiceImpl(orderRepository).createOrder(userId.toString(), orderDetails);

        // Aggregate placed: identity assigned, user set, items linked back, defaults applied.
        assertNotNull(createdOrder.getId());
        assertEquals(userId, createdOrder.getUserId());
        assertSame(createdOrder, createdOrder.getItems().get(0).getOrder());
        assertEquals(OrderStatus.PENDING, createdOrder.getStatus());
        assertFalse(createdOrder.getIsPaid());
        assertNotNull(createdOrder.getCreatedAt());
        assertNotNull(createdOrder.getUpdatedAt());
    }
}
