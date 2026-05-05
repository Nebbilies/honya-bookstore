package com.honya.bookstore.catalog.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "media", schema = "catalog")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Media {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String url;
    private String altText;
}