package com.honya.bookstore.cart.infrastructure.persistence;

import com.honya.bookstore.cart.domain.CartProcessedOrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface CartProcessedOrderEventRepository extends JpaRepository<CartProcessedOrderEvent, UUID> {

    @Modifying
    @Query(value = "INSERT INTO cart.cart_processed_order_events (id, order_id, processed_at) VALUES (gen_random_uuid(), :orderId, :processedAt) ON CONFLICT (order_id) DO NOTHING", nativeQuery = true)
    int insertIfAbsent(@Param("orderId") UUID orderId, @Param("processedAt") OffsetDateTime processedAt);
}
