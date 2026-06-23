package com.honya.bookstore.checkout.application;

import com.honya.bookstore.checkout.domain.SagaInstance;
import com.honya.bookstore.checkout.domain.SagaStatus;
import com.honya.bookstore.checkout.infrastructure.persistence.SagaInstanceRepository;
import com.honya.bookstore.checkout.web.dto.CheckoutRequestDTO;
import com.honya.bookstore.shared.integration.cart.CartClient;
import com.honya.bookstore.shared.integration.cart.CartItemSnapshot;
import com.honya.bookstore.shared.integration.cart.CartSnapshot;
import com.honya.bookstore.shared.integration.catalog.CatalogBookView;
import com.honya.bookstore.shared.integration.catalog.CatalogClient;
import com.honya.bookstore.shared.integration.order.OrderClient;
import com.honya.bookstore.shared.integration.order.OrderResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CONFLICT;

class CheckoutServiceTest {

    private final CartClient cartClient = mock(CartClient.class);
    private final CatalogClient catalogClient = mock(CatalogClient.class);
    private final OrderClient orderClient = mock(OrderClient.class);
    private final SagaInstanceRepository sagaRepository = mock(SagaInstanceRepository.class);

    private final UUID userId = UUID.randomUUID();
    private final UUID bookA = UUID.randomUUID();
    private final UUID bookB = UUID.randomUUID();

    private CheckoutService service() {
        when(sagaRepository.save(any(SagaInstance.class))).thenAnswer(invocation -> {
            SagaInstance saga = invocation.getArgument(0);
            if (saga.getId() == null) {
                saga.setId(UUID.randomUUID());
            }
            return saga;
        });
        return new CheckoutService(cartClient, catalogClient, orderClient, sagaRepository, new ObjectMapper(), 15);
    }

    private void cartHasTwoLines() {
        when(cartClient.getCheckoutSnapshot(userId.toString())).thenReturn(new CartSnapshot(userId, List.of(
                new CartItemSnapshot(bookA, 2),
                new CartItemSnapshot(bookB, 1))));
        when(catalogClient.getBook(bookA)).thenReturn(new CatalogBookView(bookA, "A", "x", "img", 100));
        when(catalogClient.getBook(bookB)).thenReturn(new CatalogBookView(bookB, "B", "x", "img", 200));
    }

    private OrderResponse orderResponse(UUID orderId, String provider, String status) {
        return new OrderResponse(orderId, "Ada", "L", "1 St", "Hanoi", null, null, null, provider, status,
                false, 400, userId, OffsetDateTime.now(), OffsetDateTime.now(), List.of());
    }

    private CheckoutRequestDTO request(String provider) {
        CheckoutRequestDTO request = new CheckoutRequestDTO();
        request.setFirstName("Ada");
        request.setProvider(provider);
        return request;
    }

    private SagaInstance lastSaved() {
        ArgumentCaptor<SagaInstance> captor = ArgumentCaptor.forClass(SagaInstance.class);
        verify(sagaRepository, org.mockito.Mockito.atLeastOnce()).save(captor.capture());
        return captor.getValue();
    }

    @Test
    void codCheckoutReservesCreatesAndCompletes() {
        cartHasTwoLines();
        UUID orderId = UUID.randomUUID();
        OrderResponse created = orderResponse(orderId, "COD", "PROCESSING");
        when(orderClient.createOrder(eq(userId.toString()), any())).thenReturn(created);

        OrderResponse result = service().checkout(userId.toString(), request("COD"));

        assertSame(created, result);
        verify(catalogClient).reserve(any(), eq(bookA), eq(2));
        verify(catalogClient).reserve(any(), eq(bookB), eq(1));
        verify(orderClient).createOrder(eq(userId.toString()), any());
        SagaInstance saga = lastSaved();
        assertEquals(SagaStatus.COMPLETED, saga.getStatus());
        assertEquals(orderId, saga.getOrderId());
        assertNull(saga.getExpiresAt());
    }

    @Test
    void vnpayCheckoutReachesAwaitingPayment() {
        cartHasTwoLines();
        OrderResponse created = orderResponse(UUID.randomUUID(), "VNPAY", "PENDING");
        when(orderClient.createOrder(eq(userId.toString()), any())).thenReturn(created);

        OrderResponse result = service().checkout(userId.toString(), request("VNPAY"));

        assertSame(created, result);
        SagaInstance saga = lastSaved();
        assertEquals(SagaStatus.AWAITING_PAYMENT, saga.getStatus());
        assertNotNull(saga.getExpiresAt());
    }

    @Test
    void insufficientStockReleasesPriorReservationsAndCompensates() {
        cartHasTwoLines();
        org.mockito.Mockito.doNothing().when(catalogClient).reserve(any(), eq(bookA), eq(2));
        org.mockito.Mockito.doThrow(new ResponseStatusException(CONFLICT, "out of stock"))
                .when(catalogClient).reserve(any(), eq(bookB), eq(1));

        assertThrows(ResponseStatusException.class,
                () -> service().checkout(userId.toString(), request("COD")));

        verify(catalogClient).release(any(), eq(bookA), eq(2));
        verify(catalogClient, never()).release(any(), eq(bookB), eq(1));
        verify(orderClient, never()).createOrder(any(), any());
        assertEquals(SagaStatus.COMPENSATED, lastSaved().getStatus());
    }

    @Test
    void orderCreationFailureReleasesAllReservations() {
        cartHasTwoLines();
        when(orderClient.createOrder(any(), any())).thenThrow(new RuntimeException("order service down"));

        assertThrows(RuntimeException.class,
                () -> service().checkout(userId.toString(), request("COD")));

        verify(catalogClient).release(any(), eq(bookA), eq(2));
        verify(catalogClient).release(any(), eq(bookB), eq(1));
        assertEquals(SagaStatus.COMPENSATED, lastSaved().getStatus());
    }
}
