package com.honya.bookstore.user.security;

import com.honya.bookstore.user.application.UserStatsService;
import com.honya.platform.security.HonyaResourceServerSecurity;
import com.honya.bookstore.user.web.UserStatsController;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserStatsController.class)
@Import(HonyaResourceServerSecurity.class)
class UserStatsSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserStatsService userStatsService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void totalUsersWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/stats/total"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void totalUsersWithCustomerRoleIsForbidden() throws Exception {
        when(jwtDecoder.decode("customer-token")).thenReturn(buildToken("customer-token", "CUSTOMER"));

        mockMvc.perform(get("/api/users/stats/total")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer customer-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void totalUsersWithStaffRoleSucceeds() throws Exception {
        when(jwtDecoder.decode("staff-token")).thenReturn(buildToken("staff-token", "STAFF"));
        when(userStatsService.totalUsers()).thenReturn(7L);

        mockMvc.perform(get("/api/users/stats/total")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer staff-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(7));
    }

    @Test
    void totalUsersWithAdminRoleSucceeds() throws Exception {
        when(jwtDecoder.decode("admin-token")).thenReturn(buildToken("admin-token", "ADMIN"));
        when(userStatsService.totalUsers()).thenReturn(3L);

        mockMvc.perform(get("/api/users/stats/total")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(3));
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
