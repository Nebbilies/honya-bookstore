package com.honya.bookstore.security;

import com.honya.bookstore.catalog.application.MediaService;
import com.honya.bookstore.catalog.web.MediaController;
import com.honya.bookstore.catalog.web.dto.response.MediaResponseDTO;
import com.honya.bookstore.config.ApiSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MediaController.class)
@Import(ApiSecurityConfig.class)
class ApiCsrfSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MediaService mediaService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void postMediaWithBearerTokenShouldSucceedWithoutCsrfToken() throws Exception {
        when(mediaService.createMedia(any())).thenReturn(MediaResponseDTO.builder()
                .id(UUID.randomUUID())
                .altText("img")
                .order(0)
                .url("http://localhost:9000/media/test_image_1")
                .createdAt(OffsetDateTime.now())
                .deletedAt(OffsetDateTime.now())
                .build());

        when(jwtDecoder.decode("valid-token")).thenReturn(Jwt.withTokenValue("valid-token")
                .header("alg", "RS256")
                .claim("sub", "admin1")
                .claim("aud", List.of("honya-api"))
                .claim("realm_access", java.util.Map.of("roles", List.of("ADMIN")))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build());

        mockMvc.perform(post("/api/media")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"altText":"images/e748fa9d-7a8d-4c2a-bc21-3bb2fd29c5a1","key":"test_image_1","order":0}
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void postMediaWithHttpBasicShouldBeUnauthorized() throws Exception {
        mockMvc.perform(post("/api/media")
                        .with(httpBasic("admin", "123456"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"altText":"images/e748fa9d-7a8d-4c2a-bc21-3bb2fd29c5a1","key":"test_image_1","order":0}
                                """))
                .andExpect(status().isUnauthorized());
    }
}
