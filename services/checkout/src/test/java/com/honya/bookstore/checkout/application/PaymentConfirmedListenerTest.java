package com.honya.bookstore.checkout.application;

import com.honya.bookstore.shared.integration.order.event.PaymentConfirmedEvent;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PaymentConfirmedListenerTest {

    @Test
    void confirmedEventConfirmsTheSaga() {
        SagaRecoveryService recovery = mock(SagaRecoveryService.class);
        UUID orderId = UUID.randomUUID();
        String payload = new ObjectMapper().writeValueAsString(new PaymentConfirmedEvent(orderId, "txn-1"));

        new PaymentConfirmedListener(recovery).on(payload);

        verify(recovery).confirmPayment(orderId);
    }
}
