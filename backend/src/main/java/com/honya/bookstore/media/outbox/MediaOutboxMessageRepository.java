package com.honya.bookstore.media.outbox;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

interface MediaOutboxMessageRepository extends JpaRepository<MediaOutboxMessage, UUID> {

    @Query("""
            select message from MediaOutboxMessage message
            where message.status in (com.honya.bookstore.media.outbox.MediaOutboxStatus.PENDING, com.honya.bookstore.media.outbox.MediaOutboxStatus.FAILED)
            and message.nextAttemptAt <= :now
            order by message.createdAt asc
            """)
    List<MediaOutboxMessage> findDueMessages(@Param("now") OffsetDateTime now, Pageable pageable);
}
