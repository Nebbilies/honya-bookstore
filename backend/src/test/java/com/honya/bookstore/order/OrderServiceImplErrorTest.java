package com.honya.bookstore.order;

import com.honya.bookstore.order.outbox.OrderOutboxWriter;
import com.honya.bookstore.shared.error.InvalidOrderStatusException;
import com.honya.bookstore.shared.error.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderServiceImplErrorTest {

    @Test
    void getOrderByIdThrowsTypedNotFound() {
        OrderRepository repository = mock(OrderRepository.class);
        OrderOutboxWriter outboxWriter = mock(OrderOutboxWriter.class);
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> new OrderServiceImpl(repository, outboxWriter).getOrderById(id));
    }

    @Test
    void updateOrderStatusThrowsTypedInvalidStatus() {
        OrderRepository repository = mock(OrderRepository.class);
        OrderOutboxWriter outboxWriter = mock(OrderOutboxWriter.class);
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(Order.builder().id(id).build()));

        assertThrows(InvalidOrderStatusException.class, () -> new OrderServiceImpl(repository, outboxWriter).updateOrderStatus(id, "bad"));
    }
}
