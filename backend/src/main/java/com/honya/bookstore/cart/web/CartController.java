package com.honya.bookstore.cart.web;

import com.honya.bookstore.cart.application.CartService;
import com.honya.bookstore.cart.domain.Cart;
import com.honya.bookstore.cart.web.dto.request.AddItemRequestDTO;
import com.honya.bookstore.cart.web.dto.request.UpdateCartItemRequestDTO;
import com.honya.bookstore.cart.web.dto.response.CartItemBookResponseDTO;
import com.honya.bookstore.cart.web.dto.response.CartItemResponseDTO;
import com.honya.bookstore.cart.web.dto.response.CartResponseDTO;
import com.honya.bookstore.catalog.api.CatalogStockApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Cart", description = "Endpoints for managing the shopping cart")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CatalogStockApi catalogStockApi;

    @Operation(summary = "Get cart", description = "Retrieve current user cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart retrieved"),
            @ApiResponse(responseCode = "404", description = "Cart not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping({"", "/me"})
    public ResponseEntity<CartResponseDTO> getCart(@RequestHeader("X-User-Id") String userId) {
        Cart cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(mapToDTO(cart));
    }

    @Operation(summary = "Create cart", description = "Create or return current user cart")
    @ApiResponse(responseCode = "200", description = "Cart ready")
    @PostMapping
    public ResponseEntity<CartResponseDTO> createCart(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) {
            return ResponseEntity.ok().build();
        }
        Cart cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(mapToDTO(cart));
    }

    @Operation(summary = "Add cart item", description = "Add book item to user cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item added to cart"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Cart or book not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartResponseDTO> addItem(
            @PathVariable UUID cartId,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestBody AddItemRequestDTO request) {
        if (userId == null) {
            return ResponseEntity.ok().build();
        }
        Cart cart = cartService.addItemToCart(userId, request.getBookId(), request.getQuantity());
        return ResponseEntity.ok(mapToDTO(cart));
    }

    @Operation(summary = "Update cart item quantity", description = "Update quantity of existing cart item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item quantity updated"),
            @ApiResponse(responseCode = "404", description = "Cart or item not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PatchMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<CartResponseDTO> updateItem(
            @PathVariable UUID cartId,
            @PathVariable UUID itemId,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestBody UpdateCartItemRequestDTO request) {
        if (userId == null) {
            return ResponseEntity.ok().build();
        }
        Cart cart = cartService.updateItemQuantity(userId, itemId, request.getQuantity());
        return ResponseEntity.ok(mapToDTO(cart));
    }

    @Operation(summary = "Remove cart item", description = "Remove item from user cart by item id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item removed from cart"),
            @ApiResponse(responseCode = "404", description = "Cart or item not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<CartResponseDTO> removeItem(
            @PathVariable UUID cartId,
            @PathVariable UUID itemId,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) {
            return ResponseEntity.ok().build();
        }
        Cart cart = cartService.removeItemFromCart(userId, itemId);
        return ResponseEntity.ok(mapToDTO(cart));
    }

    @Operation(summary = "Clear cart", description = "Remove all items from user cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cart cleared"),
            @ApiResponse(responseCode = "404", description = "Cart not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") UUID userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    private CartResponseDTO mapToDTO(Cart cart) {
        return CartResponseDTO.builder()
                .id(cart.getId())
                .ownerId(cart.getOwnerId())
                .updatedAt(cart.getUpdatedAt())
                .items(cart.getItems().stream()
                        .map(item -> CartItemResponseDTO.builder()
                                .id(item.getId())
                                .book(CartItemBookResponseDTO.builder()
                                        .id(item.getBookId())
                                        .price(catalogStockApi.getBookPrice(item.getBookId()))
                                        .build())
                                .quantity(item.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
