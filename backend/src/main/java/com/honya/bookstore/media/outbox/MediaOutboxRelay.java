package com.honya.bookstore.media.outbox;

import com.honya.bookstore.shared.integration.media.MediaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class MediaOutboxRelay {

    private final MediaOutboxMessageRepository repository;
    private final MediaEventPublisher eventPublisher;
    private final MediaOutboxBackoff backoff;

    @Scheduled(fixedDelayString = "${bookstore.media-outbox.relay-fixed-delay-ms:1000}")
    public void publishDueMessages() {
        repository.findDueMessages(OffsetDateTime.now(), PageRequest.of(0, 25))
                .forEach(this::publish);
    }

    private void publish(MediaOutboxMessage message) {
        try {
            eventPublisher.publish(message.getEventType(), message.getPayload());
            OffsetDateTime now = OffsetDateTime.now();
            message.setStatus(MediaOutboxStatus.SENT);
            message.setSentAt(now);
            message.setUpdatedAt(now);
            repository.save(message);
            log.info("Media outbox message published. id={}, eventType={}, aggregateId={}", message.getId(), message.getEventType(), message.getAggregateId());
        } catch (RuntimeException ex) {
            OffsetDateTime now = OffsetDateTime.now();
            int attempts = message.getAttempts() + 1;
            message.setAttempts(attempts);
            message.setStatus(MediaOutboxStatus.FAILED);
            message.setLastError(shortError(ex));
            message.setNextAttemptAt(now.plus(backoff.delayForAttempt(attempts)));
            message.setUpdatedAt(now);
            repository.save(message);
            log.warn("Media outbox publish failed. id={}, eventType={}, aggregateId={}, attempts={}, nextAttemptAt={}, error={}", message.getId(), message.getEventType(), message.getAggregateId(), attempts, message.getNextAttemptAt(), message.getLastError());
        }
    }

    private String shortError(RuntimeException ex) {
        String message = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        return message.length() > 500 ? message.substring(0, 500) : message;
    }
}
