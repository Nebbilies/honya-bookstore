package com.honya.bookstore.order.outbox;

import com.honya.bookstore.order.api.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderOutboxRelay {

    private final OrderOutboxMessageRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderOutboxEventSerializer serializer;
    private final OrderOutboxBackoff backoff;

    @Scheduled(fixedDelayString = "${bookstore.outbox.relay-fixed-delay-ms:1000}")
    public void publishDueMessages() {
        repository.findDueMessages(OffsetDateTime.now(), PageRequest.of(0, 25))
                .forEach(this::publish);
    }

    private void publish(OrderOutboxMessage message) {
        try {
            OrderPlacedEvent event = serializer.deserialize(message.getPayload());
            eventPublisher.publishEvent(event);
            OffsetDateTime now = OffsetDateTime.now();
            message.setStatus(OrderOutboxStatus.SENT);
            message.setSentAt(now);
            message.setUpdatedAt(now);
            repository.save(message);
            log.info("Outbox message published. id={}, eventType={}, aggregateId={}", message.getId(), message.getEventType(), message.getAggregateId());
        } catch (RuntimeException ex) {
            OffsetDateTime now = OffsetDateTime.now();
            int attempts = message.getAttempts() + 1;
            message.setAttempts(attempts);
            message.setStatus(OrderOutboxStatus.FAILED);
            message.setLastError(shortError(ex));
            message.setNextAttemptAt(now.plus(backoff.delayForAttempt(attempts)));
            message.setUpdatedAt(now);
            repository.save(message);
            log.warn("Outbox message publish failed. id={}, eventType={}, aggregateId={}, attempts={}, nextAttemptAt={}, error={}", message.getId(), message.getEventType(), message.getAggregateId(), attempts, message.getNextAttemptAt(), message.getLastError());
        }
    }

    private String shortError(RuntimeException ex) {
        String message = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        return message.length() > 500 ? message.substring(0, 500) : message;
    }
}
