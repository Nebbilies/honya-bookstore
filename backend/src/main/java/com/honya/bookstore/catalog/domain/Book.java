package com.honya.bookstore.catalog.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "books", schema = "catalog")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Book {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    private String description;
    private String author;
    private Integer price;
    private Integer pagesCount;
    private Integer yearPublished;
    private String publisher;
    private Float weight;
    private Integer stockQuantity;
    private Integer purchaseCount;
    private Float rating;

    @ManyToMany
    @JoinTable(
            name = "book_categories",
            schema = "catalog",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;
}