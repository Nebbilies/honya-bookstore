package com.honya.bookstore.checkout.application;

import com.honya.bookstore.shared.integration.order.RabbitOrderEventsConfig;
import com.honya.bookstore.shared.integration.order.event.PaymentConfirmedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class PaymentConfirmedListener {

    private final SagaRecoveryService sagaRecoveryService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = RabbitOrderEventsConfig.CHECKOUT_PAYMENT_QUEUE)
    public void on(String payload) {
        PaymentConfirmedEvent event = deserialize(payload);
        sagaRecoveryService.confirmPayment(event.orderId());
    }

    private PaymentConfirmedEvent deserialize(String payload) {
        try {
            return objectMapper.readValue(payload, PaymentConfirmedEvent.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to handle payment confirmed event", ex);
        }
    }
}
