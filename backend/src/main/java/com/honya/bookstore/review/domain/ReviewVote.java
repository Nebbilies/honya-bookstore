package com.honya.bookstore.review.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "review_votes", schema = "review")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReviewVote {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Boolean isUp;

    @Column(name = "user_id")
    private UUID voter;

    @Column(name = "review_id")
    private UUID review;
}