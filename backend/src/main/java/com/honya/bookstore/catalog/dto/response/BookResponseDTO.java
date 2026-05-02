package com.honya.bookstore.catalog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private List<CategoryResponseDTO> categories; // Return the full category details to the frontend
}