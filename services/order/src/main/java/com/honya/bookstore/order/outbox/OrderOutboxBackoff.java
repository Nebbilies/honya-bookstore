package com.honya.bookstore.order.outbox;

import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
class OrderOutboxBackoff {

    Duration delayForAttempt(int attempt) {
        int exponent = Math.max(0, attempt - 1);
        long seconds = 1L << Math.min(exponent, 9);
        return Duration.ofSeconds(Math.min(300, seconds));
    }
}
