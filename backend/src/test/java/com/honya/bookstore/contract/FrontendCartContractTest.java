package com.honya.bookstore.contract;

import com.honya.bookstore.cart.application.CartService;
import com.honya.bookstore.cart.domain.Cart;
import com.honya.bookstore.cart.domain.CartItem;
import com.honya.bookstore.cart.web.CartController;
import com.honya.bookstore.catalog.api.CatalogStockApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FrontendCartContractTest {

    private MockMvc mockMvc;
    private CartService cartService;
    private CatalogStockApi catalogStockApi;

    @BeforeEach
    void setUp() {
        cartService = mock(CartService.class);
        catalogStockApi = mock(CatalogStockApi.class);
        when(catalogStockApi.getBookPrice(any(UUID.class))).thenReturn(1000);
        CartController cartController = new CartController(cartService, catalogStockApi);

        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
    }

    @Test
    void getCartMe_returns_cart_with_nested_book_items() throws Exception {
        String userId = UUID.randomUUID().toString();
        when(cartService.getCartByUserId(userId)).thenReturn(sampleCart(UUID.fromString(userId)));

        mockMvc.perform(get("/api/cart/me")
                        .header("X-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.items[0].book.id").exists())
                .andExpect(jsonPath("$.items[0].book.price").isNumber());
    }

    @Test
    void cart_payload_items_include_nested_book_object_not_only_bookId() throws Exception {
        String userId = UUID.randomUUID().toString();
        when(cartService.getCartByUserId(userId)).thenReturn(sampleCart(UUID.fromString(userId)));

        mockMvc.perform(get("/api/cart")
                        .header("X-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.items[0].book.id").exists())
                .andExpect(jsonPath("$.items[0].book.price").isNumber());
    }

    @Test
    void frontend_cart_item_routes_are_available() throws Exception {
        UUID cartId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        mockMvc.perform(post("/api/cart"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/cart/{cartId}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"bookId":"%s","quantity":2}
                                """.formatted(bookId)))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/cart/{cartId}/items/{itemId}", cartId, itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"quantity":3}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/cart/{cartId}/items/{itemId}", cartId, itemId))
                .andExpect(status().isOk());
    }

    private Cart sampleCart(UUID userId) {
        CartItem item = CartItem.builder()
                .id(UUID.randomUUID())
                .bookId(UUID.randomUUID())
                .quantity(2)
                .build();

        return Cart.builder()
                .id(UUID.randomUUID())
                .ownerId(userId)
                .updatedAt(OffsetDateTime.parse("2026-05-04T10:15:30Z"))
                .items(List.of(item))
                .build();
    }
}
