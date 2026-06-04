package com.honya.bookstore.catalog.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "book_media", schema = "catalog")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BookMedia {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "media_id")
    private UUID mediaId;

    @Column(name = "media_url")
    private String mediaUrl;

    @Column(name = "media_alt_text")
    private String mediaAltText;

    private Boolean isCover;

    @Column(name = "media_order")
    private Integer order;
}
