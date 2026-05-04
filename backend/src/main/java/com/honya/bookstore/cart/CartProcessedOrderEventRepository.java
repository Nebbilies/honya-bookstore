package com.honya.bookstore.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface CartProcessedOrderEventRepository extends JpaRepository<CartProcessedOrderEvent, UUID> {
    boolean existsByOrderId(UUID orderId);
}
