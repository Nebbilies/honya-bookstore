package com.honya.bookstore.media.outbox;

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
@Table(name = "media_outbox_messages", schema = "media")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaOutboxMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String eventType;
    private UUID aggregateId;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    private MediaOutboxStatus status;

    private int attempts;
    private OffsetDateTime nextAttemptAt;
    private OffsetDateTime sentAt;
    private String lastError;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
