package com.honya.bookstore.catalog.infrastructure.persistence;

import com.honya.bookstore.catalog.domain.CatalogProcessedOrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CatalogProcessedOrderEventRepository extends JpaRepository<CatalogProcessedOrderEvent, UUID> {
    boolean existsByOrderId(UUID orderId);
}
