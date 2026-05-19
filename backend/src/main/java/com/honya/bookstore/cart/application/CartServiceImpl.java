package com.honya.bookstore.cart.application;

import com.honya.bookstore.cart.domain.Cart;
import com.honya.bookstore.cart.domain.CartItem;
import com.honya.bookstore.cart.infrastructure.persistence.CartRepository;
import com.honya.bookstore.catalog.api.CatalogCartSnapshot;
import com.honya.bookstore.catalog.api.CatalogStockApi;
import com.honya.bookstore.shared.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CatalogStockApi catalogStockApi;

    @Override
    @Transactional
    public Cart getCartByUserId(String userId) {
        UUID ownerId = UUID.fromString(userId);

        // Find the user's cart, or create a brand new one if it doesn't exist yet
        return cartRepository.findByOwnerId(ownerId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .ownerId(ownerId)
                            .updatedAt(OffsetDateTime.now())
                            .items(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    @Override
    @Transactional
    public Cart addItemToCart(String userId, UUID bookId, Integer quantity) {
        Cart cart = getCartByUserId(userId);
        CatalogCartSnapshot snapshot = catalogStockApi.getCartSnapshot(bookId);

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> snapshot.id().equals(item.getCatalogItemId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .bookId(bookId)
                    .catalogItemId(snapshot.id())
                    .title(snapshot.title())
                    .author(snapshot.author())
                    .imageUrl(snapshot.imageUrl())
                    .unitPrice(snapshot.price())
                    .quantity(quantity)
                    .build();
            cart.getItems().add(newItem);
        }

        cart.setUpdatedAt(OffsetDateTime.now());
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart updateItemQuantity(String userId, UUID cartItemId, Integer quantity) {
        Cart cart = getCartByUserId(userId);

        cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));

        cart.setUpdatedAt(OffsetDateTime.now());
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart removeItemFromCart(String userId, UUID cartItemId) {
        Cart cart = getCartByUserId(userId);

        cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        cart.setUpdatedAt(OffsetDateTime.now());

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(UUID userId) {
        Cart cart = cartRepository.findByOwnerId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", userId));
        cart.getItems().clear();
        cart.setUpdatedAt(OffsetDateTime.now());
        cartRepository.save(cart);
    }
}