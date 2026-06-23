package com.honya.bookstore.checkout.security;

import com.honya.bookstore.checkout.application.CheckoutService;
import com.honya.bookstore.checkout.web.CheckoutController;
import com.honya.bookstore.shared.integration.order.OrderResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CheckoutController.class)
@Import(HonyaResourceServerSecurity.class)
class CheckoutSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CheckoutService checkoutService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void checkoutWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/checkout/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void checkoutWithCustomerRoleSucceeds() throws Exception {
        UUID userId = UUID.randomUUID();
        when(jwtDecoder.decode("customer-token")).thenReturn(buildToken("customer-token", userId, "CUSTOMER"));
        when(checkoutService.checkout(anyString(), any())).thenReturn(new OrderResponse(
                UUID.randomUUID(), "Ada", "Lovelace", "12 Example Street", "London",
                null, null, null, null, "PENDING", false, 0, userId,
                OffsetDateTime.now(), OffsetDateTime.now(), List.of()));

        mockMvc.perform(post("/api/checkout/checkout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer customer-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Ada\",\"lastName\":\"Lovelace\",\"address\":\"12 Example Street\",\"city\":\"London\"}"))
                .andExpect(status().isOk());
    }

    private Jwt buildToken(String tokenValue, UUID userId, String role) {
        return Jwt.withTokenValue(tokenValue)
                .header("alg", "RS256")
                .claim("sub", userId.toString())
                .claim("aud", List.of("honya-api"))
                .claim("realm_access", Map.of("roles", List.of(role)))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }
}
