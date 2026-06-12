package com.honya.bookstore.review.application;

import com.honya.bookstore.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReviewService {
    Page<Review> getReviews(UUID bookId, Pageable pageable);
    Review getReviewById(UUID id);
    Review createReview(String userId, UUID bookId, Integer rating, String content);
    Review updateReview(String userId, UUID id, Integer rating, String content);
    void deleteReview(String userId, UUID id);
    Review voteReview(String userId, UUID reviewId, Boolean isUp);
    Review removeVote(String userId, UUID reviewId);
}
