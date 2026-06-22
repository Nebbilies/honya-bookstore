package com.honya.bookstore.cart.security;

import com.honya.bookstore.cart.application.CartService;
import com.honya.bookstore.cart.domain.Cart;
import com.honya.bookstore.cart.web.CartController;
import com.honya.platform.security.HonyaResourceServerSecurity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@Import(HonyaResourceServerSecurity.class)
class CartSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void getCartWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCartWithCustomerTokenSucceeds() throws Exception {
        String subject = UUID.randomUUID().toString();
        when(jwtDecoder.decode("customer-token")).thenReturn(buildToken("customer-token", "CUSTOMER", subject));
        when(cartService.getCartByUserId(anyString())).thenReturn(sampleCart(UUID.fromString(subject)));

        mockMvc.perform(get("/api/cart")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer customer-token"))
                .andExpect(status().isOk());
    }

    @Test
    void writeWithHttpBasicIsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/cart/{cartId}/items", UUID.randomUUID())
                        .with(httpBasic("admin", "123456"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":\"" + UUID.randomUUID() + "\",\"quantity\":1}"))
                .andExpect(status().isUnauthorized());
    }

    private Cart sampleCart(UUID userId) {
        return Cart.builder()
                .id(UUID.randomUUID())
                .ownerId(userId)
                .updatedAt(OffsetDateTime.now())
                .items(List.of())
                .build();
    }

    private Jwt buildToken(String tokenValue, String role, String subject) {
        return Jwt.withTokenValue(tokenValue)
                .header("alg", "RS256")
                .claim("sub", subject)
                .claim("aud", List.of("honya-api"))
                .claim("realm_access", Map.of("roles", List.of(role)))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }
}
