package com.honya.bookstore.article.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "articles", schema = "article")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Article {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String slug;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ElementCollection
    @CollectionTable(name = "article_tags", schema = "article", joinColumns = @JoinColumn(name = "article_id"))
    @Column(name = "tags")
    private List<String> tags;

    @Column(name = "author_id")
    private UUID authorId;

    @Column(name = "media_id")
    private UUID thumbnailId;

    @Column(name = "media_url")
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    private ArticleStatus status;

    @Column(name = "created_at")
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}