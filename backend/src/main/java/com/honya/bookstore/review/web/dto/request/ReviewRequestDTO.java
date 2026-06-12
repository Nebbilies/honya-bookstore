package com.honya.bookstore.review.web.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class ReviewRequestDTO {
    private UUID bookId;
    private Integer rating;
    private String content;
}
