package com.honya.bookstore.article.web.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleRequestDTO {
    private UUID id;
    private String title;
    private String slug;
    private String content;
    private UUID authorId;
    private UUID thumbnailId;
    private String thumbnailUrl;
    private String[] tags;
    private String status;
}
