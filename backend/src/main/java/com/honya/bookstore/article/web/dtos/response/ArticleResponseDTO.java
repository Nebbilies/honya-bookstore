package com.honya.bookstore.article.web.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponseDTO {
    private UUID id;
    private String title;
    private String slug;
    private String content;
    private UUID authorId;
    private UUID thumbnailId;
    private String thumbnailUrl;
    private String[] tags;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime deletedAt;
}
