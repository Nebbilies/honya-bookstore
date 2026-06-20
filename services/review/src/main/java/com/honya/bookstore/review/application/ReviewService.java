package com.honya.bookstore.review.application;

import com.honya.bookstore.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ReviewService {
    Page<Review> getReviewsByBook(UUID bookId, Pageable pageable);

    Review createOrUpdateReview(UUID userId, String authorName, UUID bookId, Integer rating, String content);

    void deleteReview(UUID reviewId, UUID userId);

    ReviewVoteResult vote(UUID reviewId, UUID userId, boolean isUp);

    Map<UUID, Boolean> getUserVotes(UUID userId, List<UUID> reviewIds);
}
