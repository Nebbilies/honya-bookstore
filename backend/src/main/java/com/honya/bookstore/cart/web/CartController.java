package com.honya.bookstore.cart.web;

import com.honya.bookstore.cart.application.CartService;
import com.honya.bookstore.cart.domain.Cart;
import com.honya.bookstore.cart.web.dto.request.AddItemRequestDTO;
import com.honya.bookstore.cart.web.dto.response.CartItemResponseDTO;
import com.honya.bookstore.cart.web.dto.response.CartResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart(@RequestHeader("X-User-Id") String userId) {
        Cart cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(mapToDTO(cart));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponseDTO> addItem(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody AddItemRequestDTO request) {
        Cart cart = cartService.addItemToCart(userId, request.getBookId(), request.getQuantity());
        return ResponseEntity.ok(mapToDTO(cart));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponseDTO> removeItem(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID itemId) {
        Cart cart = cartService.removeItemFromCart(userId, itemId);
        return ResponseEntity.ok(mapToDTO(cart));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") UUID userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    // Helper method to convert Entity to DTO
    private CartResponseDTO mapToDTO(Cart cart) {
        return CartResponseDTO.builder()
                .id(cart.getId())
                .ownerId(cart.getOwnerId())
                .updatedAt(cart.getUpdatedAt())
                .items(cart.getItems().stream()
                        .map(item -> CartItemResponseDTO.builder()
                                .id(item.getId())
                                .bookId(item.getBookId())
                                .quantity(item.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}