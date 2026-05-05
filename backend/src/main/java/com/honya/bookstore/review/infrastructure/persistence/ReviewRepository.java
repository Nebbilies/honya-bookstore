package com.honya.bookstore.review.infrastructure.persistence;

import com.honya.bookstore.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
}