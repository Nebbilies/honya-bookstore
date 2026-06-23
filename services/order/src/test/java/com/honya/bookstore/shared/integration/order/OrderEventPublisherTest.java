package com.honya.bookstore.shared.integration.order;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OrderEventPublisherTest {

    @Test
    void routesOrderPlacedToOrderPlacedKey() {
        RabbitTemplate rabbit = mock(RabbitTemplate.class);
        new OrderEventPublisher(rabbit).publish("ORDER_PLACED", "{}");
        verify(rabbit).convertAndSend(OrderEventsTopology.EXCHANGE, OrderEventsTopology.ORDER_PLACED, "{}");
    }

    @Test
    void routesPaymentConfirmedToPaymentConfirmedKey() {
        RabbitTemplate rabbit = mock(RabbitTemplate.class);
        new OrderEventPublisher(rabbit).publish("PAYMENT_CONFIRMED", "{}");
        verify(rabbit).convertAndSend(OrderEventsTopology.EXCHANGE, OrderEventsTopology.PAYMENT_CONFIRMED, "{}");
    }

    @Test
    void routesPaymentFailedToPaymentFailedKey() {
        RabbitTemplate rabbit = mock(RabbitTemplate.class);
        new OrderEventPublisher(rabbit).publish("PAYMENT_FAILED", "{}");
        verify(rabbit).convertAndSend(OrderEventsTopology.EXCHANGE, OrderEventsTopology.PAYMENT_FAILED, "{}");
    }
}
