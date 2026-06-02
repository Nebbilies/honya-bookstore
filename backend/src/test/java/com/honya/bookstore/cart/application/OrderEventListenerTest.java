package com.honya.bookstore.cart.application;

import tools.jackson.databind.ObjectMapper;
import com.honya.bookstore.cart.domain.CartProcessedOrderEvent;
import com.honya.bookstore.cart.infrastructure.persistence.CartProcessedOrderEventRepository;
import com.honya.bookstore.shared.integration.order.event.OrderPlacedEvent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderEventListenerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static String json(OrderPlacedEvent event) {
        return MAPPER.writeValueAsString(event);
    }

    @Test
    void newOrderEventRecordsMarkerAndClearsCart() {
        CartService cartService = mock(CartService.class);
        CartProcessedOrderEventRepository repository = mock(CartProcessedOrderEventRepository.class);
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(repository.existsByOrderId(orderId)).thenReturn(false);

        new OrderEventListener(cartService, repository).handleOrder(json(new OrderPlacedEvent(orderId, userId, List.of())));

        verify(repository).save(org.mockito.ArgumentMatchers.any(CartProcessedOrderEvent.class));
        verify(cartService).clearCart(userId);
    }

    @Test
    void duplicateOrderEventDoesNotClearCartAgain() {
        CartService cartService = mock(CartService.class);
        CartProcessedOrderEventRepository repository = mock(CartProcessedOrderEventRepository.class);
        UUID orderId = UUID.randomUUID();
        when(repository.existsByOrderId(orderId)).thenReturn(true);

        new OrderEventListener(cartService, repository).handleOrder(json(new OrderPlacedEvent(orderId, UUID.randomUUID(), List.of())));

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

        String payload = json(new OrderPlacedEvent(orderId, userId, List.of()));
        OrderEventListener listener = new OrderEventListener(cartService, repository);
        assertThrows(RuntimeException.class, () -> listener.handleOrder(payload));
    }
}
