package com.honya.bookstore.cart.api;

import java.util.UUID;

public interface CartApi {
    void clearCart(UUID userId);
}
