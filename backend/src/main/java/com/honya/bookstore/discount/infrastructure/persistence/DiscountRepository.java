package com.honya.bookstore.discount.infrastructure.persistence;

import com.honya.bookstore.discount.domain.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Integer> {
}