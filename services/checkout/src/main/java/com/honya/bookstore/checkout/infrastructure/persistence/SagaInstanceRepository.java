package com.honya.bookstore.checkout.infrastructure.persistence;

import com.honya.bookstore.checkout.domain.SagaInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SagaInstanceRepository extends JpaRepository<SagaInstance, UUID> {
}
