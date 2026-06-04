package com.honya.bookstore.cart.application;

import tools.jackson.databind.ObjectMapper;
import com.honya.bookstore.cart.infrastructure.persistence.CartProcessedOrderEventRepository;
import com.honya.bookstore.shared.integration.order.RabbitOrderIntegrationConfig;
import com.honya.bookstore.shared.integration.order.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Component("cartOrderEventListener")
@RequiredArgsConstructor
public class OrderEventListener {

    private final CartService cartService;
    private final CartProcessedOrderEventRepository processedOrderEventRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = RabbitOrderIntegrationConfig.CART_QUEUE)
    @Transactional
    public void handleOrder(String payload) {
        OrderPlacedEvent event = deserialize(payload);

        if (processedOrderEventRepository.insertIfAbsent(event.orderId(), OffsetDateTime.now()) == 0) {
            return;
        }

        cartService.clearCart(event.userId());
    }

    private OrderPlacedEvent deserialize(String payload) {
        try {
            return objectMapper.readValue(payload, OrderPlacedEvent.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to handle order integration event", ex);
        }
    }
}
