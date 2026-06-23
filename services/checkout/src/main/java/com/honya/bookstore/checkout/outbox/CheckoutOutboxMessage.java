package com.honya.bookstore.checkout.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "checkout_outbox_messages", schema = "checkout")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutOutboxMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String eventType;
    private UUID aggregateId;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    private CheckoutOutboxStatus status;

    private int attempts;
    private OffsetDateTime nextAttemptAt;
    private OffsetDateTime sentAt;
    private String lastError;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
