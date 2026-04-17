package com.honya.bookstore.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "review_votes")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReviewVote {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Boolean isUp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User voter;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;
}