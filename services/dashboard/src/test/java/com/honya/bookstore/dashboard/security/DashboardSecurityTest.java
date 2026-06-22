package com.honya.bookstore.dashboard.security;

import com.honya.bookstore.dashboard.infrastructure.client.UserStatsClient;
import com.honya.bookstore.dashboard.web.DashboardController;
import com.honya.bookstore.shared.integration.order.OrderStatsClient;
import com.honya.platform.security.HonyaResourceServerSecurity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@Import(HonyaResourceServerSecurity.class)
class DashboardSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderStatsClient orderStatsClient;

    @MockitoBean
    private UserStatsClient userStatsClient;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void summaryWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void summaryWithCustomerRoleIsForbidden() throws Exception {
        when(jwtDecoder.decode("customer-token")).thenReturn(buildToken("customer-token", "CUSTOMER"));

        mockMvc.perform(get("/api/dashboard/summary")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer customer-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void summaryWithStaffRoleSucceeds() throws Exception {
        when(jwtDecoder.decode("staff-token")).thenReturn(buildToken("staff-token", "STAFF"));
        when(orderStatsClient.salesThisMonth()).thenReturn(1000L);
        when(orderStatsClient.newCustomersThisMonth()).thenReturn(5L);
        when(orderStatsClient.ordersThisMonth()).thenReturn(20L);
        when(userStatsClient.totalUsers()).thenReturn(50L);

        mockMvc.perform(get("/api/dashboard/summary")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer staff-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salesThisMonth").value(1000))
                .andExpect(jsonPath("$.totalUsers").value(50));
    }

    @Test
    void bestSellersWithAdminRoleSucceeds() throws Exception {
        when(jwtDecoder.decode("admin-token")).thenReturn(buildToken("admin-token", "ADMIN"));
        when(orderStatsClient.bestSellers(any(), anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/api/dashboard/best-sellers")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer admin-token"))
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
