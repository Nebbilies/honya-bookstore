package com.honya.bookstore.catalog.web.dto.request;

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
public class BookRequestDTO {
    private String title;
    private String description;
    private String author;
    private Integer price;
    private Integer pagesCount;
    private Integer yearPublished;
    private String publisher;
    private Float weight;
    private Integer stockQuantity;
    private List<UUID> categoryIds;
    private List<BookMediaRequestDTO> media;
}