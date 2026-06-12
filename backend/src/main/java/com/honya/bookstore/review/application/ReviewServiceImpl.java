package com.honya.bookstore.review.application;

import com.honya.bookstore.catalog.api.CatalogStockApi;
import com.honya.bookstore.review.domain.Review;
import com.honya.bookstore.review.domain.ReviewVote;
import com.honya.bookstore.review.infrastructure.persistence.ReviewRepository;
import com.honya.bookstore.review.infrastructure.persistence.ReviewVoteRepository;
import com.honya.bookstore.shared.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewVoteRepository reviewVoteRepository;
    private final CatalogStockApi catalogStockApi;

    @Override
    public Page<Review> getReviews(UUID bookId, Pageable pageable) {
        if (bookId == null) {
            return reviewRepository.findAll(pageable);
        }
        return reviewRepository.findByBookId(bookId, pageable);
    }

    @Override
    public Review getReviewById(UUID id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", id));
    }

    @Override
    @Transactional
    public Review createReview(String userId, UUID bookId, Integer rating, String content) {
        if (bookId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bookId is required");
        }
        validateRating(rating);
        validateContent(content);
        catalogStockApi.getBookPrice(bookId);

        OffsetDateTime now = OffsetDateTime.now();
        Review review = Review.builder()
                .bookId(bookId)
                .authorId(UUID.fromString(userId))
                .rating(rating)
                .content(content)
                .voteCount(0)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public Review updateReview(String userId, UUID id, Integer rating, String content) {
        validateRating(rating);
        validateContent(content);

        Review review = getReviewById(id);
        assertOwner(userId, review);

        review.setRating(rating);
        review.setContent(content);
        review.setUpdatedAt(OffsetDateTime.now());
        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void deleteReview(String userId, UUID id) {
        Review review = getReviewById(id);
        assertOwner(userId, review);
        reviewVoteRepository.deleteByReview(id);
        reviewRepository.delete(review);
    }

    @Override
    @Transactional
    public Review voteReview(String userId, UUID reviewId, Boolean isUp) {
        if (isUp == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "isUp is required");
        }

        Review review = getReviewById(reviewId);
        UUID voter = UUID.fromString(userId);

        reviewVoteRepository.findByReviewAndVoter(reviewId, voter)
                .ifPresentOrElse(
                        vote -> updateVote(review, vote, isUp),
                        () -> createVote(review, voter, isUp)
                );

        review.setUpdatedAt(OffsetDateTime.now());
        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public Review removeVote(String userId, UUID reviewId) {
        Review review = getReviewById(reviewId);
        UUID voter = UUID.fromString(userId);

        reviewVoteRepository.findByReviewAndVoter(reviewId, voter).ifPresent(vote -> {
            review.setVoteCount(currentVoteCount(review) - voteWeight(vote.getIsUp()));
            reviewVoteRepository.delete(vote);
        });

        review.setUpdatedAt(OffsetDateTime.now());
        return reviewRepository.save(review);
    }

    private void createVote(Review review, UUID voter, Boolean isUp) {
        reviewVoteRepository.save(ReviewVote.builder()
                .review(review.getId())
                .voter(voter)
                .isUp(isUp)
                .build());
        review.setVoteCount(currentVoteCount(review) + voteWeight(isUp));
    }

    private void updateVote(Review review, ReviewVote vote, Boolean isUp) {
        if (vote.getIsUp().equals(isUp)) {
            return;
        }
        review.setVoteCount(currentVoteCount(review) - voteWeight(vote.getIsUp()) + voteWeight(isUp));
        vote.setIsUp(isUp);
        reviewVoteRepository.save(vote);
    }

    private int currentVoteCount(Review review) {
        return review.getVoteCount() == null ? 0 : review.getVoteCount();
    }

    private int voteWeight(Boolean isUp) {
        return Boolean.TRUE.equals(isUp) ? 1 : -1;
    }

    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rating must be between 1 and 5");
        }
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "content is required");
        }
    }

    private void assertOwner(String userId, Review review) {
        if (!review.getAuthorId().equals(UUID.fromString(userId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the review author can change this review");
        }
    }
}
