package com.honya.bookstore.ticket;

import com.honya.bookstore.ticket.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID; // Added UUID import

@Entity
@Table(name = "tickets", schema = "ticket")
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

    @Column(name = "placed_by_id")
    private UUID placedById;

    @Column(name = "respondent_id")
    private UUID respondentId;
}