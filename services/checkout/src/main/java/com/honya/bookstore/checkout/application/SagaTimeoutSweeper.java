package com.honya.bookstore.checkout.application;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class SagaTimeoutSweeper {

    private final SagaRecoveryService sagaRecoveryService;

    @Scheduled(fixedDelayString = "${bookstore.saga.sweep-fixed-delay-ms:5000}")
    public void sweep() {
        sagaRecoveryService.compensateExpired(OffsetDateTime.now());
    }
}
