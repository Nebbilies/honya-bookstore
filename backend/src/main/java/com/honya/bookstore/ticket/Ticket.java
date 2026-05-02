package com.honya.bookstore.domain.entity;

import com.honya.bookstore.domain.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "tickets")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Ticket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String category;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Column(columnDefinition = "TEXT")
    private String response;

    private OffsetDateTime createdAt;
    private OffsetDateTime respondedAt;

    @ManyToOne
    @JoinColumn(name = "placed_by_id")
    private User placedBy;

    @ManyToOne
    @JoinColumn(name = "respondent_id")
    private User respondent;
}