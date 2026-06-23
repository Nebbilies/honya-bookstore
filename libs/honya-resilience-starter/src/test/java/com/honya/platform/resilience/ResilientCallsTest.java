package com.honya.platform.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResilientCallsTest {

    private ResilientCalls resilientCalls;

    @BeforeEach
    void setUp() {
        CircuitBreakerConfig breakerConfig = CircuitBreakerConfig.custom()
                .slidingWindowSize(4)
                .minimumNumberOfCalls(4)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .ignoreException(ResilientExceptions::isBusinessError)
                .build();
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(1))
                .retryOnException(ResilientExceptions::isRetryable)
                .build();
        resilientCalls = new ResilientCalls(
                CircuitBreakerRegistry.of(breakerConfig),
                RetryRegistry.of(retryConfig));
    }

    @Test
    void nonRetryableCallInvokesActionOnce() {
        AtomicInteger calls = new AtomicInteger();
        Supplier<String> action = () -> {
            calls.incrementAndGet();
            throw new HttpServerErrorStub();
        };

        assertThatThrownBy(() -> resilientCalls.call("no-retry", false, action));
        assertThat(calls.get()).isEqualTo(1);
    }

    @Test
    void retryableCallRetriesOnServerError() {
        AtomicInteger calls = new AtomicInteger();
        Supplier<String> action = () -> {
            calls.incrementAndGet();
            throw new HttpServerErrorStub();
        };

        assertThatThrownBy(() -> resilientCalls.call("retry", true, action));
        assertThat(calls.get()).isEqualTo(3);
    }

    @Test
    void businessErrorIsNotRetriedAndDoesNotOpenBreaker() {
        AtomicInteger calls = new AtomicInteger();
        Supplier<String> action = () -> {
            calls.incrementAndGet();
            throw HttpClientErrorException.create(HttpStatus.CONFLICT, "conflict", null, null, null);
        };

        for (int i = 0; i < 10; i++) {
            assertThatThrownBy(() -> resilientCalls.call("business", true, action))
                    .isInstanceOf(HttpClientErrorException.class);
        }
        assertThat(calls.get()).isEqualTo(10);
    }

    @Test
    void breakerOpensAfterFailuresThenFailsFastWith503() {
        Supplier<String> failing = () -> {
            throw new HttpServerErrorStub();
        };

        for (int i = 0; i < 4; i++) {
            assertThatThrownBy(() -> resilientCalls.call("open", false, failing));
        }

        assertThatThrownBy(() -> resilientCalls.call("open", false, () -> "should not run"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE));
    }

    @Test
    void callOrDefaultReturnsFallbackOnFailure() {
        String result = resilientCalls.callOrDefault("fallback", true,
                () -> {
                    throw new HttpServerErrorStub();
                },
                () -> "default");

        assertThat(result).isEqualTo("default");
    }

    private static final class HttpServerErrorStub extends RuntimeException {
    }
}
