package com.honya.bookstore.catalog.web;

import com.honya.bookstore.catalog.application.BookService;
import com.honya.bookstore.catalog.config.CatalogPublicEndpoints;
import com.honya.bookstore.shared.error.InsufficientStockException;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookStockController.class)
@Import({HonyaResourceServerSecurity.class, CatalogPublicEndpoints.class})
class BookStockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private String body(UUID sagaId, int quantity) {
        return "{\"sagaId\":\"" + sagaId + "\",\"quantity\":" + quantity + "}";
    }

    @Test
    void reserveWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/books/{id}/reserve", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UUID.randomUUID(), 3)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void reserveWithCustomerTokenSucceeds() throws Exception {
        when(jwtDecoder.decode("customer-token")).thenReturn(buildToken("customer-token", "CUSTOMER"));

        mockMvc.perform(post("/api/books/{id}/reserve", UUID.randomUUID())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer customer-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UUID.randomUUID(), 3)))
                .andExpect(status().isOk());
    }

    @Test
    void reserveReturnsConflictWhenInsufficientStock() throws Exception {
        when(jwtDecoder.decode("customer-token")).thenReturn(buildToken("customer-token", "CUSTOMER"));
        UUID bookId = UUID.randomUUID();
        doThrow(new InsufficientStockException(bookId, "Demo", 5, 2))
                .when(bookService).reserveStock(any(), eq(bookId), eq(5));

        mockMvc.perform(post("/api/books/{id}/reserve", bookId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer customer-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UUID.randomUUID(), 5)))
                .andExpect(status().isConflict());
    }

    @Test
    void releaseWithCustomerTokenSucceeds() throws Exception {
        when(jwtDecoder.decode("customer-token")).thenReturn(buildToken("customer-token", "CUSTOMER"));

        mockMvc.perform(post("/api/books/{id}/release", UUID.randomUUID())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer customer-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UUID.randomUUID(), 3)))
                .andExpect(status().isOk());
    }

    private Jwt buildToken(String tokenValue, String role) {
        return Jwt.withTokenValue(tokenValue)
                .header("alg", "RS256")
                .claim("sub", UUID.randomUUID().toString())
                .claim("aud", List.of("honya-api"))
                .claim("realm_access", Map.of("roles", List.of(role)))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }
}
