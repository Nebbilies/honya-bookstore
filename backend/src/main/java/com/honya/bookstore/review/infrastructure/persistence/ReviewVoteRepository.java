package com.honya.bookstore.review.infrastructure.persistence;

import com.honya.bookstore.review.domain.ReviewVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ReviewVoteRepository extends JpaRepository<ReviewVote, UUID> {
}