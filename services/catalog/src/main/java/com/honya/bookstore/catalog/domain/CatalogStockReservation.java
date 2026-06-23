package com.honya.bookstore.catalog.domain;

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
@Table(name = "catalog_stock_reservations", schema = "catalog")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogStockReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID sagaId;

    @Column(nullable = false)
    private UUID bookId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String status;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}
