package com.honya.bookstore.cart.application;

import com.honya.bookstore.cart.domain.Cart;

import java.util.UUID;

public interface CartService {
    Cart getCartByUserId(String userId);
    Cart addItemToCart(String userId, UUID bookId, Integer quantity);
    Cart updateItemQuantity(String userId, UUID cartItemId, Integer quantity);
    Cart removeItemFromCart(String userId, UUID cartItemId);
    void clearCart(UUID userId);
    void updateSnapshotForCatalogItem(UUID catalogItemId, String title, String author, String imageUrl, Integer unitPrice);
    void removeItemsByCatalogItemId(UUID catalogItemId);
}