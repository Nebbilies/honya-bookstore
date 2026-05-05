package com.honya.bookstore.cart;

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

        // Check if the book is already in the cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getBookId().equals(bookId))
                .findFirst();

        if (existingItem.isPresent()) {
            // Just increase the quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            // Add a new item
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .bookId(bookId)
                    .quantity(quantity)
                    .build();
            cart.getItems().add(newItem);
        }

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