package com.honya.bookstore.catalog.outbox;

import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
class CatalogOutboxBackoff {

    Duration delayForAttempt(int attempt) {
        int exponent = Math.max(0, attempt - 1);
        long seconds = 1L << Math.min(exponent, 9);
        return Duration.ofSeconds(Math.min(300, seconds));
    }
}
