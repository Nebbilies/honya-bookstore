package com.honya.bookstore.catalog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface CatalogProcessedOrderEventRepository extends JpaRepository<CatalogProcessedOrderEvent, UUID> {
    boolean existsByOrderId(UUID orderId);
}
