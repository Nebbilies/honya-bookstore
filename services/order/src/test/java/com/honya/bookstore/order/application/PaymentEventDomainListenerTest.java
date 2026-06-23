package com.honya.bookstore.order.application;

import com.honya.bookstore.order.domain.PaymentConfirmedDomainEvent;
import com.honya.bookstore.order.domain.PaymentFailedDomainEvent;
import com.honya.bookstore.order.outbox.OrderOutboxWriter;
import com.honya.bookstore.shared.integration.order.event.PaymentConfirmedEvent;
import com.honya.bookstore.shared.integration.order.event.PaymentFailedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PaymentEventDomainListenerTest {

    @Test
    void relaysPaymentConfirmedToOutbox() {
        OrderOutboxWriter outboxWriter = mock(OrderOutboxWriter.class);
        UUID orderId = UUID.randomUUID();

        new PaymentEventDomainListener(outboxWriter)
                .on(new PaymentConfirmedDomainEvent(orderId, "txn-1"));

        ArgumentCaptor<PaymentConfirmedEvent> captor = ArgumentCaptor.forClass(PaymentConfirmedEvent.class);
        verify(outboxWriter).enqueue(eq("PAYMENT_CONFIRMED"), eq(orderId), captor.capture());
        assertEquals(orderId, captor.getValue().orderId());
        assertEquals("txn-1", captor.getValue().transactionNo());
    }

    @Test
    void relaysPaymentFailedToOutbox() {
        OrderOutboxWriter outboxWriter = mock(OrderOutboxWriter.class);
        UUID orderId = UUID.randomUUID();

        new PaymentEventDomainListener(outboxWriter)
                .on(new PaymentFailedDomainEvent(orderId, "VNPAY_24"));

        ArgumentCaptor<PaymentFailedEvent> captor = ArgumentCaptor.forClass(PaymentFailedEvent.class);
        verify(outboxWriter).enqueue(eq("PAYMENT_FAILED"), eq(orderId), captor.capture());
        assertEquals(orderId, captor.getValue().orderId());
        assertEquals("VNPAY_24", captor.getValue().reason());
    }
}
