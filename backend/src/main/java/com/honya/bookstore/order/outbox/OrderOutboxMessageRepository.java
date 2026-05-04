package com.honya.bookstore.order.outbox;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

interface OrderOutboxMessageRepository extends JpaRepository<OrderOutboxMessage, UUID> {

    @Query("""
            select message from OrderOutboxMessage message
            where message.status in (com.honya.bookstore.order.outbox.OrderOutboxStatus.PENDING, com.honya.bookstore.order.outbox.OrderOutboxStatus.FAILED)
            and message.nextAttemptAt <= :now
            order by message.createdAt asc
            """)
    List<OrderOutboxMessage> findDueMessages(@Param("now") OffsetDateTime now, Pageable pageable);
}
