package com.honya.bookstore.order.application;

import com.honya.bookstore.order.domain.Order;
import com.honya.bookstore.order.infrastructure.persistence.OrderItemBookRepository;
import com.honya.bookstore.order.infrastructure.persistence.OrderRepository;
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
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        OrderItemBookRepository bookRepository = mock(OrderItemBookRepository.class);

        assertThrows(ResourceNotFoundException.class, () -> new OrderServiceImpl(repository, bookRepository).getOrderById(id));
    }

    @Test
    void updateOrderStatusThrowsTypedInvalidStatus() {
        OrderRepository repository = mock(OrderRepository.class);
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(Order.builder().id(id).build()));
        OrderItemBookRepository bookRepository = mock(OrderItemBookRepository.class);

        assertThrows(InvalidOrderStatusException.class, () -> new OrderServiceImpl(repository, bookRepository).updateOrderStatus(id, "bad"));
    }
}
