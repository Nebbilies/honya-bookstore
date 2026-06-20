package com.honya.bookstore.review;

import com.honya.bookstore.review.domain.Review;
import com.honya.bookstore.review.domain.ReviewVote;
import com.honya.bookstore.review.infrastructure.persistence.ReviewRepository;
import com.honya.bookstore.review.infrastructure.persistence.ReviewVoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://issuer.example/realms/honya",
        "app.security.jwt.jwk-set-uri=https://issuer.example/realms/honya/protocol/openid-connect/certs",
        "app.security.jwt.audience=honya-api"
})
class ReviewPersistenceTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void flywayProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ReviewVoteRepository reviewVoteRepository;

    @Test
    void flywayMigratesAndReviewWithVotePersists() {
        UUID bookId = UUID.randomUUID();
        UUID voterId = UUID.randomUUID();

        Review saved = reviewRepository.save(Review.builder()
                .bookId(bookId)
                .authorId(UUID.randomUUID())
                .authorName("Reviewer")
                .rating(5)
                .content("Great")
                .voteCount(1)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build());

        reviewVoteRepository.save(ReviewVote.builder()
                .review(saved.getId())
                .voter(voterId)
                .isUp(true)
                .build());

        assertTrue(reviewRepository.findById(saved.getId()).isPresent());
        assertEquals(1, reviewRepository.findByBookId(bookId, PageRequest.of(0, 10)).getTotalElements());
        assertTrue(reviewVoteRepository.findByReviewAndVoter(saved.getId(), voterId).isPresent());
    }
}
