package com.honya.bookstore.review.web.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class ReviewResponseDTO {
    private UUID id;
    private UUID bookId;
    private UUID userId;
    private Integer rating;
    private Integer voteCount;
    private String content;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
