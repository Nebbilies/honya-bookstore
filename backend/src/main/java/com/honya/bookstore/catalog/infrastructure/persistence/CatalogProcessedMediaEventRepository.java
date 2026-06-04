package com.honya.bookstore.catalog.infrastructure.persistence;

import com.honya.bookstore.catalog.domain.CatalogProcessedMediaEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CatalogProcessedMediaEventRepository extends JpaRepository<CatalogProcessedMediaEvent, UUID> {
    boolean existsByEventId(UUID eventId);
}
