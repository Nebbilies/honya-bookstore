package com.honya.bookstore.review.web.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ReviewResponseDTO(
        UUID id,
        UUID bookId,
        UUID userId,
        String authorName,
        Integer rating,
        String content,
        Integer voteCount,
        String userVote,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
