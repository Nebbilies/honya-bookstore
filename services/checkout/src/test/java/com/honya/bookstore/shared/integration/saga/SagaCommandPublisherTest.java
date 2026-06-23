package com.honya.bookstore.shared.integration.saga;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SagaCommandPublisherTest {

    @Test
    void routesReleaseStockToStockReleaseKey() {
        RabbitTemplate rabbit = mock(RabbitTemplate.class);
        new SagaCommandPublisher(rabbit).publish("RELEASE_STOCK", "{}");
        verify(rabbit).convertAndSend(SagaCommandsTopology.EXCHANGE, SagaCommandsTopology.STOCK_RELEASE, "{}");
    }

    @Test
    void routesCancelOrderToOrderCancelKey() {
        RabbitTemplate rabbit = mock(RabbitTemplate.class);
        new SagaCommandPublisher(rabbit).publish("CANCEL_ORDER", "{}");
        verify(rabbit).convertAndSend(SagaCommandsTopology.EXCHANGE, SagaCommandsTopology.ORDER_CANCEL, "{}");
    }
}
