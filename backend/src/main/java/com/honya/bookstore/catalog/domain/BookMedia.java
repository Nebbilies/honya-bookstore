package com.honya.bookstore.catalog.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "book_media")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BookMedia {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "media_id")
    private Media media;

    private Boolean isCover;

    @Column(name = "media_order")
    private Integer order;
}