package com.honya.bookstore.order.outbox;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderOutboxBackoffTest {

    @Test
    void delayUsesExponentialBackoffCappedAtFiveMinutes() {
        OrderOutboxBackoff backoff = new OrderOutboxBackoff();

        assertEquals(Duration.ofSeconds(1), backoff.delayForAttempt(1));
        assertEquals(Duration.ofSeconds(2), backoff.delayForAttempt(2));
        assertEquals(Duration.ofSeconds(4), backoff.delayForAttempt(3));
        assertEquals(Duration.ofSeconds(8), backoff.delayForAttempt(4));
        assertEquals(Duration.ofSeconds(16), backoff.delayForAttempt(5));
        assertEquals(Duration.ofSeconds(300), backoff.delayForAttempt(20));
    }
}
