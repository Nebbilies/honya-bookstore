package com.honya.bookstore.review.application;

import com.honya.bookstore.review.domain.Review;
import com.honya.bookstore.review.domain.ReviewVote;
import com.honya.bookstore.review.infrastructure.persistence.ReviewRepository;
import com.honya.bookstore.review.infrastructure.persistence.ReviewVoteRepository;
import com.honya.bookstore.shared.error.InvalidReviewException;
import com.honya.bookstore.shared.error.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReviewServiceImplTest {

    private ReviewRepository reviewRepository;
    private ReviewVoteRepository voteRepository;
    private ReviewServiceImpl service;

    @BeforeEach
    void setUp() {
        reviewRepository = mock(ReviewRepository.class);
        voteRepository = mock(ReviewVoteRepository.class);
        service = new ReviewServiceImpl(reviewRepository, voteRepository);
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void createsNewReviewWhenNoneExists() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        when(reviewRepository.findByBookIdAndAuthorId(bookId, userId)).thenReturn(Optional.empty());

        Review review = service.createOrUpdateReview(userId, "Minh Nebbilies", bookId, 4, "  Solid read  ");

        assertEquals(4, review.getRating());
        assertEquals("Solid read", review.getContent());
        assertEquals(0, review.getVoteCount());
        assertEquals(userId, review.getAuthorId());
        assertEquals("Minh Nebbilies", review.getAuthorName());
        assertEquals(bookId, review.getBookId());
    }

    @Test
    void updatesExistingReviewInPlace() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        Review existing = Review.builder().id(UUID.randomUUID()).bookId(bookId).authorId(userId)
                .rating(2).content("meh").voteCount(7).build();
        when(reviewRepository.findByBookIdAndAuthorId(bookId, userId)).thenReturn(Optional.of(existing));

        Review review = service.createOrUpdateReview(userId, "Minh Nebbilies", bookId, 5, "Changed my mind");

        assertEquals(existing.getId(), review.getId());
        assertEquals(5, review.getRating());
        assertEquals("Changed my mind", review.getContent());
        assertEquals(7, review.getVoteCount());
    }

    @Test
    void rejectsOutOfRangeRating() {
        assertThrows(InvalidReviewException.class,
                () -> service.createOrUpdateReview(UUID.randomUUID(), "Tester", UUID.randomUUID(), 6, "x"));
    }

    @Test
    void rejectsBlankContent() {
        assertThrows(InvalidReviewException.class,
                () -> service.createOrUpdateReview(UUID.randomUUID(), "Tester", UUID.randomUUID(), 3, "   "));
    }

    @Test
    void deleteByNonOwnerThrowsNotFound() {
        UUID reviewId = UUID.randomUUID();
        Review review = Review.builder().id(reviewId).authorId(UUID.randomUUID()).build();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        assertThrows(ResourceNotFoundException.class, () -> service.deleteReview(reviewId, UUID.randomUUID()));
        verify(reviewRepository, never()).delete(any());
    }

    @Test
    void deleteByOwnerRemovesVotesThenReview() {
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Review review = Review.builder().id(reviewId).authorId(userId).build();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        service.deleteReview(reviewId, userId);

        verify(voteRepository).deleteByReview(reviewId);
        verify(reviewRepository).delete(review);
    }

    @Test
    void firstUpvoteAddsOne() {
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(
                Review.builder().id(reviewId).voteCount(0).build()));
        when(voteRepository.findByReviewAndVoter(reviewId, userId)).thenReturn(Optional.empty());

        ReviewVoteResult result = service.vote(reviewId, userId, true);

        assertEquals(1, result.voteCount());
        assertEquals("UP", result.userVote());
        verify(voteRepository).save(any(ReviewVote.class));
    }

    @Test
    void repeatedUpvoteTogglesOff() {
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ReviewVote existing = ReviewVote.builder().id(UUID.randomUUID()).review(reviewId).voter(userId).isUp(true).build();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(
                Review.builder().id(reviewId).voteCount(1).build()));
        when(voteRepository.findByReviewAndVoter(reviewId, userId)).thenReturn(Optional.of(existing));

        ReviewVoteResult result = service.vote(reviewId, userId, true);

        assertEquals(0, result.voteCount());
        assertNull(result.userVote());
        verify(voteRepository).delete(existing);
    }

    @Test
    void oppositeVoteFlipsByTwo() {
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ReviewVote existing = ReviewVote.builder().id(UUID.randomUUID()).review(reviewId).voter(userId).isUp(true).build();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(
                Review.builder().id(reviewId).voteCount(1).build()));
        when(voteRepository.findByReviewAndVoter(reviewId, userId)).thenReturn(Optional.of(existing));

        ReviewVoteResult result = service.vote(reviewId, userId, false);

        assertEquals(-1, result.voteCount());
        assertEquals("DOWN", result.userVote());
    }
}
