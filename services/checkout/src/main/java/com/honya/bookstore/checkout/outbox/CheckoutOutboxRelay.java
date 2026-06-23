package com.honya.bookstore.checkout.outbox;

import com.honya.bookstore.shared.integration.saga.SagaCommandPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckoutOutboxRelay {

    private final CheckoutOutboxMessageRepository repository;
    private final SagaCommandPublisher publisher;

    @Scheduled(fixedDelayString = "${bookstore.outbox.relay-fixed-delay-ms:1000}")
    public void publishDueMessages() {
        repository.findDueMessages(OffsetDateTime.now(), PageRequest.of(0, 25))
                .forEach(this::publish);
    }

    private void publish(CheckoutOutboxMessage message) {
        OffsetDateTime now = OffsetDateTime.now();
        try {
            publisher.publish(message.getEventType(), message.getPayload());
            message.setStatus(CheckoutOutboxStatus.SENT);
            message.setSentAt(now);
            message.setUpdatedAt(now);
            repository.save(message);
        } catch (RuntimeException ex) {
            int attempts = message.getAttempts() + 1;
            message.setAttempts(attempts);
            message.setStatus(CheckoutOutboxStatus.FAILED);
            message.setLastError(shortError(ex));
            message.setNextAttemptAt(now.plus(Duration.ofSeconds(Math.min(60, (long) Math.pow(2, attempts)))));
            message.setUpdatedAt(now);
            repository.save(message);
            log.warn("Saga command publish failed. id={}, eventType={}, attempts={}, error={}",
                    message.getId(), message.getEventType(), attempts, message.getLastError());
        }
    }

    private String shortError(RuntimeException ex) {
        String message = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        return message.length() > 500 ? message.substring(0, 500) : message;
    }
}
