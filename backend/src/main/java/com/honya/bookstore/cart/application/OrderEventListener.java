package com.honya.bookstore.cart.application;

import com.honya.bookstore.cart.domain.CartProcessedOrderEvent;
import com.honya.bookstore.cart.infrastructure.persistence.CartProcessedOrderEventRepository;
import com.honya.bookstore.order.api.event.OrderPlacedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

@Component("catalogOrderEventListener")
@RequiredArgsConstructor
public class OrderEventListener {

    private final CartService cartService;
    private final CartProcessedOrderEventRepository processedOrderEventRepository;

    @EventListener
    @Transactional
    public void handleOrder(OrderPlacedEvent event) {
        if (processedOrderEventRepository.existsByOrderId(event.getOrderId())) {
            return;
        }

        processedOrderEventRepository.save(CartProcessedOrderEvent.builder()
                .orderId(event.getOrderId())
                .processedAt(OffsetDateTime.now())
                .build());

        cartService.clearCart(event.getUserId());
    }
}