package com.honya.bookstore.order.application;

import com.honya.bookstore.shared.integration.saga.RabbitSagaCommandConfig;
import com.honya.bookstore.shared.integration.saga.command.CancelOrderCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class SagaCommandListener {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitSagaCommandConfig.ORDER_QUEUE)
    public void onCancelOrder(String payload) {
        CancelOrderCommand command = deserialize(payload);
        orderService.cancelOrder(command.orderId());
    }

    private CancelOrderCommand deserialize(String payload) {
        try {
            return objectMapper.readValue(payload, CancelOrderCommand.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to handle saga command", ex);
        }
    }
}
