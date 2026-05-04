package com.honya.bookstore.order;

import com.honya.bookstore.order.api.event.OrderPlacedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderServiceImplTest {

    @Test
    void createOrderLinksItemsAndPublishesOrderEvent() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        Order orderDetails = Order.builder()
                .firstName("Ada")
                .lastName("Lovelace")
                .address("12 Example Street")
                .city("London")
                .items(List.of(OrderItem.builder()
                        .bookId(bookId)
                        .quantity(2)
                        .price(100)
                        .build()))
                .build();

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(UUID.randomUUID());
            return order;
        });

        Order createdOrder = new OrderServiceImpl(orderRepository, eventPublisher)
                .createOrder(userId.toString(), orderDetails);

        assertEquals(userId, createdOrder.getUserId());
        assertSame(createdOrder, createdOrder.getItems().get(0).getOrder());

        ArgumentCaptor<OrderPlacedEvent> eventCaptor = ArgumentCaptor.forClass(OrderPlacedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(createdOrder.getId(), eventCaptor.getValue().getOrderId());
        assertEquals(userId, eventCaptor.getValue().getUserId());
        assertEquals(bookId, eventCaptor.getValue().getItems().get(0).getBookId());
        assertEquals(2, eventCaptor.getValue().getItems().get(0).getQuantity());
    }
}
