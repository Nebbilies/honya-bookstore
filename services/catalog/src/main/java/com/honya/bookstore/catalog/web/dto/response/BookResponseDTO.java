package com.honya.bookstore.catalog.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDTO {
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
    private List<CategoryResponseDTO> categories;
    private List<BookMediaResponseDTO> media;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime deletedAt;
}