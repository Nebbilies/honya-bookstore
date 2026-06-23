package com.honya.bookstore.checkout.outbox;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface CheckoutOutboxMessageRepository extends JpaRepository<CheckoutOutboxMessage, UUID> {

    @Query("""
            select message from CheckoutOutboxMessage message
            where message.status in (com.honya.bookstore.checkout.outbox.CheckoutOutboxStatus.PENDING,
                                     com.honya.bookstore.checkout.outbox.CheckoutOutboxStatus.FAILED)
              and message.nextAttemptAt <= :now
            order by message.createdAt asc
            """)
    List<CheckoutOutboxMessage> findDueMessages(@Param("now") OffsetDateTime now, Pageable pageable);
}
