package com.honya.bookstore.checkout.application;

import com.honya.bookstore.shared.integration.order.RabbitOrderEventsConfig;
import com.honya.bookstore.shared.integration.order.event.PaymentRetriedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class PaymentRetriedListener {

    private final SagaRecoveryService sagaRecoveryService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = RabbitOrderEventsConfig.CHECKOUT_PAYMENT_RETRIED_QUEUE)
    public void on(String payload) {
        PaymentRetriedEvent event = deserialize(payload);
        sagaRecoveryService.extendPaymentWindow(event.orderId());
    }

    private PaymentRetriedEvent deserialize(String payload) {
        try {
            return objectMapper.readValue(payload, PaymentRetriedEvent.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to handle payment retried event", ex);
        }
    }
}
