package com.honya.bookstore.catalog.infrastructure.persistence;

import com.honya.bookstore.catalog.domain.CatalogProcessedMediaEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface CatalogProcessedMediaEventRepository extends JpaRepository<CatalogProcessedMediaEvent, UUID> {

    @Modifying
    @Query(value = "INSERT INTO catalog.catalog_processed_media_events (id, event_id, media_id, processed_at) VALUES (gen_random_uuid(), :eventId, :mediaId, :processedAt) ON CONFLICT (event_id) DO NOTHING", nativeQuery = true)
    int insertIfAbsent(@Param("eventId") UUID eventId, @Param("mediaId") UUID mediaId, @Param("processedAt") OffsetDateTime processedAt);
}
