package com.honya.bookstore.catalog.application;

import tools.jackson.databind.ObjectMapper;
import com.honya.bookstore.catalog.domain.CatalogProcessedOrderEvent;
import com.honya.bookstore.catalog.infrastructure.persistence.CatalogProcessedOrderEventRepository;
import com.honya.bookstore.shared.integration.order.event.OrderItemEventDTO;
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
    void newOrderEventRecordsMarkerAndReducesStock() {
        BookService bookService = mock(BookService.class);
        CatalogProcessedOrderEventRepository repository = mock(CatalogProcessedOrderEventRepository.class);
        UUID orderId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        when(repository.existsByOrderId(orderId)).thenReturn(false);

        new OrderEventListener(bookService, repository).handleOrder(json(new OrderPlacedEvent(
                orderId,
                UUID.randomUUID(),
                List.of(new OrderItemEventDTO(bookId, 2))
        )));

        verify(repository).save(org.mockito.ArgumentMatchers.any(CatalogProcessedOrderEvent.class));
        verify(bookService).reduceStock(bookId, 2);
    }

    @Test
    void duplicateOrderEventDoesNotReduceStockAgain() {
        BookService bookService = mock(BookService.class);
        CatalogProcessedOrderEventRepository repository = mock(CatalogProcessedOrderEventRepository.class);
        UUID orderId = UUID.randomUUID();
        when(repository.existsByOrderId(orderId)).thenReturn(true);

        new OrderEventListener(bookService, repository).handleOrder(json(new OrderPlacedEvent(
                orderId,
                UUID.randomUUID(),
                List.of(new OrderItemEventDTO(UUID.randomUUID(), 2))
        )));

        verify(repository, never()).save(org.mockito.ArgumentMatchers.any(CatalogProcessedOrderEvent.class));
        verify(bookService, never()).reduceStock(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    void stockReductionFailurePropagatesForRelayRetry() {
        BookService bookService = mock(BookService.class);
        CatalogProcessedOrderEventRepository repository = mock(CatalogProcessedOrderEventRepository.class);
        UUID orderId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        when(repository.existsByOrderId(orderId)).thenReturn(false);
        org.mockito.Mockito.doThrow(new RuntimeException("stock failed")).when(bookService).reduceStock(bookId, 2);

        String payload = json(new OrderPlacedEvent(
                orderId,
                UUID.randomUUID(),
                List.of(new OrderItemEventDTO(bookId, 2))
        ));
        OrderEventListener listener = new OrderEventListener(bookService, repository);
        assertThrows(RuntimeException.class, () -> listener.handleOrder(payload));
    }
}
