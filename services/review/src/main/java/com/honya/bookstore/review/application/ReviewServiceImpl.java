package com.honya.bookstore.review.application;

import com.honya.bookstore.review.domain.Review;
import com.honya.bookstore.review.domain.ReviewVote;
import com.honya.bookstore.review.infrastructure.persistence.ReviewRepository;
import com.honya.bookstore.review.infrastructure.persistence.ReviewVoteRepository;
import com.honya.bookstore.shared.error.InvalidReviewException;
import com.honya.bookstore.shared.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class ReviewServiceImpl implements ReviewService {

    private static final int MAX_CONTENT_LENGTH = 300;

    private final ReviewRepository reviewRepository;
    private final ReviewVoteRepository reviewVoteRepository;

    @Override
    public Page<Review> getReviewsByBook(UUID bookId, Pageable pageable) {
        return reviewRepository.findByBookId(bookId, pageable);
    }

    @Override
    @Transactional
    public Review createOrUpdateReview(UUID userId, String authorName, UUID bookId, Integer rating, String content) {
        if (bookId == null) {
            throw new InvalidReviewException("Book is required");
        }
        if (rating == null || rating < 1 || rating > 5) {
            throw new InvalidReviewException("Rating must be between 1 and 5");
        }
        String trimmed = content == null ? "" : content.trim();
        if (trimmed.isEmpty()) {
            throw new InvalidReviewException("Review content must not be empty");
        }
        if (trimmed.length() > MAX_CONTENT_LENGTH) {
            throw new InvalidReviewException("Review content must not exceed " + MAX_CONTENT_LENGTH + " characters");
        }

        OffsetDateTime now = OffsetDateTime.now();
        Review review = reviewRepository.findByBookIdAndAuthorId(bookId, userId)
                .orElseGet(() -> Review.builder()
                        .bookId(bookId)
                        .authorId(userId)
                        .voteCount(0)
                        .createdAt(now)
                        .build());

        review.setRating(rating);
        review.setContent(trimmed);
        review.setAuthorName(authorName);
        review.setUpdatedAt(now);

        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void deleteReview(UUID reviewId, UUID userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", reviewId));
        if (!userId.equals(review.getAuthorId())) {
            throw new ResourceNotFoundException("Review", reviewId);
        }

        reviewVoteRepository.deleteByReview(reviewId);
        reviewRepository.delete(review);
    }

    @Override
    @Transactional
    public ReviewVoteResult vote(UUID reviewId, UUID userId, boolean isUp) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", reviewId));

        int count = review.getVoteCount() == null ? 0 : review.getVoteCount();
        String userVote;

        ReviewVote existing = reviewVoteRepository.findByReviewAndVoter(reviewId, userId).orElse(null);
        if (existing == null) {
            count += isUp ? 1 : -1;
            reviewVoteRepository.save(ReviewVote.builder()
                    .review(reviewId)
                    .voter(userId)
                    .isUp(isUp)
                    .build());
            userVote = isUp ? "UP" : "DOWN";
        } else if (Boolean.TRUE.equals(existing.getIsUp()) == isUp) {
            // Same direction again toggles the vote off.
            count -= isUp ? 1 : -1;
            reviewVoteRepository.delete(existing);
            userVote = null;
        } else {
            // Opposite direction flips the existing vote.
            count += isUp ? 2 : -2;
            existing.setIsUp(isUp);
            reviewVoteRepository.save(existing);
            userVote = isUp ? "UP" : "DOWN";
        }

        review.setVoteCount(count);
        reviewRepository.save(review);

        return new ReviewVoteResult(count, userVote);
    }

    @Override
    public Map<UUID, Boolean> getUserVotes(UUID userId, List<UUID> reviewIds) {
        if (userId == null || reviewIds == null || reviewIds.isEmpty()) {
            return Map.of();
        }
        return reviewVoteRepository.findByVoterAndReviewIn(userId, reviewIds).stream()
                .collect(Collectors.toMap(ReviewVote::getReview, ReviewVote::getIsUp));
    }
}
