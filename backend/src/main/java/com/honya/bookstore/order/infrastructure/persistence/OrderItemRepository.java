package com.honya.bookstore.order.infrastructure.persistence;

import com.honya.bookstore.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
}