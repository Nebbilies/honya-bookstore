package com.honya.platform.resilience;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Supplier;

public class ResilientCalls {

    private final CircuitBreakerRegistry circuitBreakers;
    private final RetryRegistry retries;

    public ResilientCalls(CircuitBreakerRegistry circuitBreakers, RetryRegistry retries) {
        this.circuitBreakers = circuitBreakers;
        this.retries = retries;
    }

    public <T> T call(String name, boolean retryable, Supplier<T> action) {
        try {
            return decorate(name, retryable, action).get();
        } catch (CallNotPermittedException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    name + " is temporarily unavailable", ex);
        }
    }

    public void run(String name, boolean retryable, Runnable action) {
        call(name, retryable, () -> {
            action.run();
            return null;
        });
    }

    public <T> T callOrDefault(String name, boolean retryable, Supplier<T> action, Supplier<T> fallback) {
        try {
            return decorate(name, retryable, action).get();
        } catch (RuntimeException ex) {
            return fallback.get();
        }
    }

    private <T> Supplier<T> decorate(String name, boolean retryable, Supplier<T> action) {
        CircuitBreaker breaker = circuitBreakers.circuitBreaker(name);
        Supplier<T> decorated = CircuitBreaker.decorateSupplier(breaker, action);
        if (retryable) {
            Retry retry = retries.retry(name);
            decorated = Retry.decorateSupplier(retry, decorated);
        }
        return decorated;
    }
}
