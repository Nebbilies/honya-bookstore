package com.honya.bookstore.review.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "reviews", schema = "review")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Integer rating;
    private Integer voteCount;
    private String content;

    @Column(name = "user_id")
    private UUID authorId;

    @Column(name = "book_id")
    private UUID bookId;
}