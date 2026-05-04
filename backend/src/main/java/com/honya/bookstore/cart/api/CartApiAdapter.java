package com.honya.bookstore.cart.api;

import com.honya.bookstore.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CartApiAdapter implements CartApi {

    private final CartService cartService;

    @Override
    public void clearCart(UUID userId) {
        cartService.clearCart(userId);
    }
}
