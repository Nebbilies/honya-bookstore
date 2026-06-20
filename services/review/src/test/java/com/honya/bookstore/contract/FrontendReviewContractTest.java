package com.honya.bookstore.contract;

import com.honya.bookstore.review.application.ReviewService;
import com.honya.bookstore.review.domain.Review;
import com.honya.bookstore.review.web.ReviewController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FrontendReviewContractTest {

    private MockMvc mockMvc;
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewService = mock(ReviewService.class);
        ReviewController controller = new ReviewController(reviewService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new NullJwtResolver())
                .build();
    }

    private static class NullJwtResolver implements HandlerMethodArgumentResolver {
        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return Jwt.class.equals(parameter.getParameterType());
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
            return null;
        }
    }

    @Test
    void getReviews_returns_data_meta_and_review_fields() throws Exception {
        Page<Review> page = new PageImpl<>(List.of(sampleReview()));
        when(reviewService.getReviewsByBook(any(), any())).thenReturn(page);
        when(reviewService.getUserVotes(any(), any())).thenReturn(Map.of());

        mockMvc.perform(get("/api/reviews?bookId=" + UUID.randomUUID() + "&page=1&limit=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.meta.totalItems").exists())
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].authorName").value("Reviewer"))
                .andExpect(jsonPath("$.data[0].rating").value(4))
                .andExpect(jsonPath("$.data[0].content").value("Nice book"))
                .andExpect(jsonPath("$.data[0].voteCount").value(3))
                .andExpect(jsonPath("$.data[0].createdAt").exists());
    }

    private Review sampleReview() {
        return Review.builder()
                .id(UUID.randomUUID())
                .bookId(UUID.randomUUID())
                .authorId(UUID.randomUUID())
                .authorName("Reviewer")
                .rating(4)
                .content("Nice book")
                .voteCount(3)
                .createdAt(OffsetDateTime.parse("2026-05-04T10:15:30Z"))
                .updatedAt(OffsetDateTime.parse("2026-05-04T10:15:30Z"))
                .build();
    }
}
