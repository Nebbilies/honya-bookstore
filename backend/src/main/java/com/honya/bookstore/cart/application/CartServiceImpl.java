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

    // handler when calls ProductDetailsChangedEvent
    @Override
    @Transactional
    public void updateSnapshotForCatalogItem(UUID catalogItemId, String title, String author, String imageUrl, Integer unitPrice) {
        OffsetDateTime now = OffsetDateTime.now();
        cartRepository.findAllByCatalogItemId(catalogItemId).forEach(cart -> {
            cart.getItems().stream()
                    .filter(item -> catalogItemId.equals(item.getCatalogItemId()))
                    .forEach(item -> {
                        if (title != null) {
                            item.setTitle(title);
                        }
                        if (author != null) {
                            item.setAuthor(author);
                        }
                        if (imageUrl != null) {
                            item.setImageUrl(imageUrl);
                        }
                        if (unitPrice != null) {
                            item.setUnitPrice(unitPrice);
                        }
                    });
            cart.setUpdatedAt(now);
            cartRepository.save(cart);
        });
    }

    // handler when calls ProductRemovedEvent
    @Override
    @Transactional
    public void removeItemsByCatalogItemId(UUID catalogItemId) {
        OffsetDateTime now = OffsetDateTime.now();
        cartRepository.findAllByCatalogItemId(catalogItemId).forEach(cart -> {
            boolean removed = cart.getItems().removeIf(item -> catalogItemId.equals(item.getCatalogItemId()));
            if (removed) {
                cart.setUpdatedAt(now);
                cartRepository.save(cart);
            }
        });
    }
}