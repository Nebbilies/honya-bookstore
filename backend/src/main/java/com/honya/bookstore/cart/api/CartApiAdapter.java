package com.honya.bookstore.cart.api;

import com.honya.bookstore.cart.domain.Cart;
import com.honya.bookstore.cart.application.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CartApiAdapter implements CartApi {

    private final CartService cartService;

    @Override
    public CartSnapshot getCheckoutSnapshot(String userId) {
        Cart cart = cartService.getCartByUserId(userId);
        return new CartSnapshot(cart.getOwnerId(), cart.getItems().stream()
                .map(item -> new CartItemSnapshot(item.getBookId(), item.getQuantity()))
                .collect(Collectors.toList()));
    }

    @Override
    public void clearCart(UUID userId) {
        cartService.clearCart(userId);
    }

    @Override
    public void addItemToCart(String userId, UUID bookId, Integer quantity) {
        cartService.addItemToCart(userId, bookId, quantity);
    }
}
