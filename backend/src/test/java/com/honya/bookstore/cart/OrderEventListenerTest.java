package com.honya.bookstore.cart;

import com.honya.bookstore.order.api.event.OrderPlacedEvent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderEventListenerTest {

    @Test
    void newOrderEventRecordsMarkerAndClearsCart() {
        CartService cartService = mock(CartService.class);
        CartProcessedOrderEventRepository repository = mock(CartProcessedOrderEventRepository.class);
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(repository.existsByOrderId(orderId)).thenReturn(false);

        new OrderEventListener(cartService, repository).handleOrder(new OrderPlacedEvent(orderId, userId, List.of()));

        verify(repository).save(org.mockito.ArgumentMatchers.any(CartProcessedOrderEvent.class));
        verify(cartService).clearCart(userId);
    }

    @Test
    void duplicateOrderEventDoesNotClearCartAgain() {
        CartService cartService = mock(CartService.class);
        CartProcessedOrderEventRepository repository = mock(CartProcessedOrderEventRepository.class);
        UUID orderId = UUID.randomUUID();
        when(repository.existsByOrderId(orderId)).thenReturn(true);

        new OrderEventListener(cartService, repository).handleOrder(new OrderPlacedEvent(orderId, UUID.randomUUID(), List.of()));

        verify(repository, never()).save(org.mockito.ArgumentMatchers.any(CartProcessedOrderEvent.class));
        verify(cartService, never()).clearCart(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void clearFailurePropagatesForRelayRetry() {
        CartService cartService = mock(CartService.class);
        CartProcessedOrderEventRepository repository = mock(CartProcessedOrderEventRepository.class);
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(repository.existsByOrderId(orderId)).thenReturn(false);
        org.mockito.Mockito.doThrow(new RuntimeException("clear failed")).when(cartService).clearCart(userId);

        assertThrows(RuntimeException.class, () -> new OrderEventListener(cartService, repository)
                .handleOrder(new OrderPlacedEvent(orderId, userId, List.of())));
    }
}
