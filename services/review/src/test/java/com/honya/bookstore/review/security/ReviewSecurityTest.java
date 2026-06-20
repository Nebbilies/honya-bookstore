package com.honya.bookstore.review.security;

import com.honya.bookstore.review.application.ReviewService;
import com.honya.bookstore.review.application.ReviewVoteResult;
import com.honya.bookstore.review.config.ReviewSecurityConfig;
import com.honya.bookstore.review.domain.Review;
import com.honya.bookstore.review.web.ReviewController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@Import(ReviewSecurityConfig.class)
class ReviewSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void listReviewsIsPublicWithoutToken() throws Exception {
        when(reviewService.getReviewsByBook(any(), any())).thenReturn(emptyPage());
        when(reviewService.getUserVotes(any(), any())).thenReturn(Map.of());

        mockMvc.perform(get("/api/reviews?bookId=" + UUID.randomUUID()))
                .andExpect(status().isOk());
    }

    @Test
    void createReviewWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"bookId":"%s","rating":5,"content":"good"}
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createReviewWithCustomerBearerSucceedsWithoutCsrfToken() throws Exception {
        UUID userId = UUID.randomUUID();
        when(jwtDecoder.decode("customer-token")).thenReturn(buildToken("customer-token", "CUSTOMER", userId.toString()));
        when(reviewService.createOrUpdateReview(any(), any(), any(), any(), any()))
                .thenReturn(Review.builder().id(UUID.randomUUID()).authorId(userId).rating(5).voteCount(0).build());

        mockMvc.perform(post("/api/reviews")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer customer-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"bookId":"%s","rating":5,"content":"good"}
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isOk());
    }

    @Test
    void voteWithCustomerBearerSucceeds() throws Exception {
        UUID userId = UUID.randomUUID();
        when(jwtDecoder.decode("customer-token")).thenReturn(buildToken("customer-token", "CUSTOMER", userId.toString()));
        when(reviewService.vote(any(), any(), anyBoolean())).thenReturn(new ReviewVoteResult(1, "UP"));

        mockMvc.perform(post("/api/reviews/" + UUID.randomUUID() + "/vote")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer customer-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":"UP"}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void deleteReviewWithCustomerBearerSucceeds() throws Exception {
        UUID userId = UUID.randomUUID();
        when(jwtDecoder.decode("customer-token")).thenReturn(buildToken("customer-token", "CUSTOMER", userId.toString()));

        mockMvc.perform(delete("/api/reviews/" + UUID.randomUUID())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer customer-token"))
                .andExpect(status().isNoContent());
    }

    private Page<Review> emptyPage() {
        return new PageImpl<>(List.of());
    }

    private Jwt buildToken(String tokenValue, String role, String subject) {
        return Jwt.withTokenValue(tokenValue)
                .header("alg", "RS256")
                .claim("sub", subject)
                .claim("name", "Test Customer")
                .claim("aud", List.of("honya-api"))
                .claim("realm_access", Map.of("roles", List.of(role)))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }
}
