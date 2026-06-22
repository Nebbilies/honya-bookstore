package com.honya.bookstore.catalog.outbox;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

interface CatalogOutboxMessageRepository extends JpaRepository<CatalogOutboxMessage, UUID> {

    @Query("""
            select message from CatalogOutboxMessage message
            where message.status in (com.honya.bookstore.catalog.outbox.CatalogOutboxStatus.PENDING, com.honya.bookstore.catalog.outbox.CatalogOutboxStatus.FAILED)
            and message.nextAttemptAt <= :now
            order by message.createdAt asc
            """)
    List<CatalogOutboxMessage> findDueMessages(@Param("now") OffsetDateTime now, Pageable pageable);
}
