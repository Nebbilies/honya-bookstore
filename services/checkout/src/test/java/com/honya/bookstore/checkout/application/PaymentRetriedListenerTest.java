package com.honya.bookstore.checkout.application;

import com.honya.bookstore.shared.integration.order.event.PaymentRetriedEvent;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PaymentRetriedListenerTest {

    @Test
    void retriedEventExtendsThePaymentWindow() {
        SagaRecoveryService recovery = mock(SagaRecoveryService.class);
        UUID orderId = UUID.randomUUID();
        String payload = new ObjectMapper().writeValueAsString(new PaymentRetriedEvent(orderId));

        new PaymentRetriedListener(recovery).on(payload);

        verify(recovery).extendPaymentWindow(orderId);
    }
}
