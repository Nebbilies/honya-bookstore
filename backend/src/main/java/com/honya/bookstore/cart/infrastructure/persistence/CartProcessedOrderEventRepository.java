package com.honya.bookstore.cart.infrastructure.persistence;

import com.honya.bookstore.cart.domain.CartProcessedOrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartProcessedOrderEventRepository extends JpaRepository<CartProcessedOrderEvent, UUID> {
    boolean existsByOrderId(UUID orderId);
}
