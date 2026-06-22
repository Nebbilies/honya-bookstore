package com.honya.bookstore.order.security;

import com.honya.bookstore.order.api.OrderApi;
import com.honya.bookstore.order.api.OrderStatsApi;
import com.honya.bookstore.order.application.OrderService;
import com.honya.bookstore.order.config.OrderPublicEndpoints;
import com.honya.bookstore.order.infrastructure.payment.VnPayProperties;
import com.honya.bookstore.order.infrastructure.payment.VnPayUrlBuilder;
import com.honya.bookstore.order.web.OrderController;
import com.honya.bookstore.order.web.OrderInternalController;
import com.honya.bookstore.order.web.OrderStatsController;
import com.honya.bookstore.order.web.VnPayIpnController;
import com.honya.platform.security.HonyaResourceServerSecurity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {OrderController.class, OrderStatsController.class, VnPayIpnController.class, OrderInternalController.class})
@Import({HonyaResourceServerSecurity.class, OrderPublicEndpoints.class})
class OrderSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;
    @MockitoBean
    private OrderStatsApi orderStatsApi;
    @MockitoBean
    private OrderApi orderApi;
    @MockitoBean
    private com.honya.bookstore.shared.integration.cart.CartClient cartClient;
    @MockitoBean
    private com.honya.bookstore.shared.integration.catalog.CatalogClient catalogClient;
    @MockitoBean
    private VnPayUrlBuilder vnPayUrlBuilder;
    @MockitoBean
    private VnPayProperties vnPayProperties;
    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void vnpayReturnIsPublic() throws Exception {
        when(vnPayUrlBuilder.isValidSignature(any())).thenReturn(false);
        when(vnPayProperties.getReturnUrl()).thenReturn("http://localhost:3000/result");

        mockMvc.perform(get("/api/orders/payment/vnpay/return"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void getMyOrdersWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/orders/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllOrdersWithCustomerRoleIsForbidden() throws Exception {
        when(jwtDecoder.decode("customer-token")).thenReturn(buildToken("customer-token", "CUSTOMER"));

        mockMvc.perform(get("/api/orders").header(HttpHeaders.AUTHORIZATION, "Bearer customer-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllOrdersWithStaffRoleSucceeds() throws Exception {
        when(jwtDecoder.decode("staff-token")).thenReturn(buildToken("staff-token", "STAFF"));
        when(orderService.searchOrders(any(), any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/orders").header(HttpHeaders.AUTHORIZATION, "Bearer staff-token"))
                .andExpect(status().isOk());
    }

    @Test
    void statsWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/orders/stats/sales-this-month"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void statsWithStaffRoleSucceeds() throws Exception {
        when(jwtDecoder.decode("staff-token")).thenReturn(buildToken("staff-token", "ADMIN"));
        when(orderStatsApi.salesThisMonth()).thenReturn(42L);

        mockMvc.perform(get("/api/orders/stats/sales-this-month").header(HttpHeaders.AUTHORIZATION, "Bearer staff-token"))
                .andExpect(status().isOk());
    }

    @Test
    void internalCreateWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/orders/internal")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
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
