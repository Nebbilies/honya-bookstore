package com.honya.bookstore.catalog.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "media", schema = "catalog")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Media {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String url;
    private String altText;
    private String key;

    @Column(name = "display_order")
    private Integer order;

    private OffsetDateTime createdAt;
    private OffsetDateTime deletedAt;
}