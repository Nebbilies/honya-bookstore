package com.honya.bookstore.cart.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "cart_processed_order_events", schema = "cart")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartProcessedOrderEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private UUID orderId;

    private OffsetDateTime processedAt;
}
