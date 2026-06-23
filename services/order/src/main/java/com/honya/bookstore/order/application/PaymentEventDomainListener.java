package com.honya.bookstore.order.application;

import com.honya.bookstore.order.domain.PaymentConfirmedDomainEvent;
import com.honya.bookstore.order.domain.PaymentFailedDomainEvent;
import com.honya.bookstore.order.outbox.OrderOutboxWriter;
import com.honya.bookstore.shared.integration.order.event.PaymentConfirmedEvent;
import com.honya.bookstore.shared.integration.order.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Bridges the in-process payment domain events raised by the Order aggregate
 * to the cross-service payment integration events on the outbox.
 */
@Component
@RequiredArgsConstructor
class PaymentEventDomainListener {

    private final OrderOutboxWriter outboxWriter;

    @EventListener
    void on(PaymentConfirmedDomainEvent event) {
        outboxWriter.enqueue("PAYMENT_CONFIRMED", event.orderId(),
                new PaymentConfirmedEvent(event.orderId(), event.transactionNo()));
    }

    @EventListener
    void on(PaymentFailedDomainEvent event) {
        outboxWriter.enqueue("PAYMENT_FAILED", event.orderId(),
                new PaymentFailedEvent(event.orderId(), event.reason()));
    }
}
