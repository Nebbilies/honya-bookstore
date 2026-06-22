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
@Table(name = "catalog_processed_media_events", schema = "catalog")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogProcessedMediaEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private UUID eventId;

    @Column(nullable = false)
    private UUID mediaId;

    private OffsetDateTime processedAt;
}
