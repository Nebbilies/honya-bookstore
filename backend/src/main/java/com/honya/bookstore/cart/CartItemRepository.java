package com.honya.bookstore.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
interface CartItemRepository extends JpaRepository<CartItem, UUID> {
}