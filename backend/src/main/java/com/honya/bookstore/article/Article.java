package com.honya.bookstore.article;

import jakarta.persistence.*;
import lombok.*;
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
}