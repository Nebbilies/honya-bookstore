package com.honya.bookstore.checkout.infrastructure.persistence;

import com.honya.bookstore.checkout.domain.SagaInstance;
import com.honya.bookstore.checkout.domain.SagaStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SagaInstanceRepository extends JpaRepository<SagaInstance, UUID> {

    Optional<SagaInstance> findByOrderId(UUID orderId);

    List<SagaInstance> findByStatusAndExpiresAtBefore(SagaStatus status, OffsetDateTime time);
}
