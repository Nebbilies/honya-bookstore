package com.honya.bookstore.cart;

import com.honya.bookstore.order.api.event.OrderPlacedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component("catalogOrderEventListener")
@RequiredArgsConstructor
public class OrderEventListener {

    private final CartService cartService;

    @EventListener
    public void handleOrder(OrderPlacedEvent event) {
        cartService.clearCart(event.getUserId());
    }
}