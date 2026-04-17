package com.honya.bookstore.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "articles")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Article {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String slug;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ElementCollection
    private List<String> tags;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @OneToOne
    @JoinColumn(name = "media_id")
    private Media thumbnail;
}