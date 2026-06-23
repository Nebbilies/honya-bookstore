package com.honya.bookstore.order.application;

import com.honya.bookstore.shared.integration.saga.command.CancelOrderCommand;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SagaCommandListenerTest {

    @Test
    void cancelCommandCancelsTheOrder() {
        OrderService orderService = mock(OrderService.class);
        ObjectMapper mapper = new ObjectMapper();
        UUID orderId = UUID.randomUUID();
        String payload = mapper.writeValueAsString(new CancelOrderCommand(UUID.randomUUID(), orderId));

        new SagaCommandListener(orderService, mapper).onCancelOrder(payload);

        verify(orderService).cancelOrder(orderId);
    }
}
