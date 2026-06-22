package com.honya.bookstore.catalog.application;

import tools.jackson.databind.ObjectMapper;
import com.honya.bookstore.catalog.infrastructure.persistence.CatalogProcessedOrderEventRepository;
import com.honya.bookstore.shared.integration.order.event.OrderItemEventDTO;
import com.honya.bookstore.shared.integration.order.event.OrderPlacedEvent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
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
    void newOrderEventReducesStock() {
        BookService bookService = mock(BookService.class);
        CatalogProcessedOrderEventRepository repository = mock(CatalogProcessedOrderEventRepository.class);
        UUID orderId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        when(repository.insertIfAbsent(eq(orderId), any())).thenReturn(1);

        new OrderEventListener(bookService, repository).handleOrder(json(new OrderPlacedEvent(
                orderId,
                UUID.randomUUID(),
                List.of(new OrderItemEventDTO(bookId, 2))
        )));

        verify(bookService).reduceStock(bookId, 2);
    }

    @Test
    void duplicateOrderEventDoesNotReduceStockAgain() {
        BookService bookService = mock(BookService.class);
        CatalogProcessedOrderEventRepository repository = mock(CatalogProcessedOrderEventRepository.class);
        UUID orderId = UUID.randomUUID();
        when(repository.insertIfAbsent(eq(orderId), any())).thenReturn(0);

        new OrderEventListener(bookService, repository).handleOrder(json(new OrderPlacedEvent(
                orderId,
                UUID.randomUUID(),
                List.of(new OrderItemEventDTO(UUID.randomUUID(), 2))
        )));

        verify(bookService, never()).reduceStock(any(), anyInt());
    }

    @Test
    void stockReductionFailurePropagatesForRelayRetry() {
        BookService bookService = mock(BookService.class);
        CatalogProcessedOrderEventRepository repository = mock(CatalogProcessedOrderEventRepository.class);
        UUID orderId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        when(repository.insertIfAbsent(eq(orderId), any())).thenReturn(1);
        doThrow(new RuntimeException("stock failed")).when(bookService).reduceStock(bookId, 2);

        String payload = json(new OrderPlacedEvent(
                orderId,
                UUID.randomUUID(),
                List.of(new OrderItemEventDTO(bookId, 2))
        ));
        OrderEventListener listener = new OrderEventListener(bookService, repository);
        assertThrows(RuntimeException.class, () -> listener.handleOrder(payload));
    }
}
