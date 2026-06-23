package com.honya.bookstore.checkout.application;

import com.honya.bookstore.checkout.domain.SagaInstance;
import com.honya.bookstore.checkout.domain.SagaStatus;
import com.honya.bookstore.checkout.infrastructure.persistence.SagaInstanceRepository;
import com.honya.bookstore.checkout.outbox.CheckoutOutboxWriter;
import com.honya.bookstore.shared.integration.cart.CartItemSnapshot;
import com.honya.bookstore.shared.integration.saga.command.CancelOrderCommand;
import com.honya.bookstore.shared.integration.saga.command.ReleaseStockCommand;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SagaRecoveryServiceTest {

    private final SagaInstanceRepository sagaRepository = mock(SagaInstanceRepository.class);
    private final CheckoutOutboxWriter outboxWriter = mock(CheckoutOutboxWriter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SagaRecoveryService service = new SagaRecoveryService(sagaRepository, outboxWriter, objectMapper);

    @Test
    void confirmPaymentCompletesAwaitingSaga() {
        UUID orderId = UUID.randomUUID();
        SagaInstance saga = SagaInstance.builder().id(UUID.randomUUID()).orderId(orderId).status(SagaStatus.AWAITING_PAYMENT).build();
        when(sagaRepository.findByOrderId(orderId)).thenReturn(Optional.of(saga));

        service.confirmPayment(orderId);

        assertEquals(SagaStatus.COMPLETED, saga.getStatus());
        verify(sagaRepository).save(saga);
    }

    @Test
    void confirmPaymentIgnoresSagaNotAwaitingPayment() {
        UUID orderId = UUID.randomUUID();
        SagaInstance saga = SagaInstance.builder().id(UUID.randomUUID()).orderId(orderId).status(SagaStatus.COMPLETED).build();
        when(sagaRepository.findByOrderId(orderId)).thenReturn(Optional.of(saga));

        service.confirmPayment(orderId);

        assertEquals(SagaStatus.COMPLETED, saga.getStatus());
        verify(sagaRepository, never()).save(any());
    }

    @Test
    void compensateExpiredEnqueuesReleaseAndCancelThenMarksCompensated() {
        UUID sagaId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        String lines = objectMapper.writeValueAsString(List.of(new CartItemSnapshot(bookId, 2)));
        SagaInstance saga = SagaInstance.builder()
                .id(sagaId).orderId(orderId).status(SagaStatus.AWAITING_PAYMENT).lines(lines).build();
        when(sagaRepository.findByStatusAndExpiresAtBefore(eq(SagaStatus.AWAITING_PAYMENT), any(OffsetDateTime.class)))
                .thenReturn(List.of(saga));

        service.compensateExpired(OffsetDateTime.now());

        verify(outboxWriter).enqueue(eq("RELEASE_STOCK"), eq(sagaId), any(ReleaseStockCommand.class));
        verify(outboxWriter).enqueue(eq("CANCEL_ORDER"), eq(sagaId), any(CancelOrderCommand.class));
        assertEquals(SagaStatus.COMPENSATED, saga.getStatus());
    }
}
