package com.honya.bookstore.review.infrastructure.persistence;

import com.honya.bookstore.review.domain.ReviewVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewVoteRepository extends JpaRepository<ReviewVote, UUID> {
    Optional<ReviewVote> findByReviewAndVoter(UUID review, UUID voter);

    @Transactional
    void deleteByReview(UUID review);
}
