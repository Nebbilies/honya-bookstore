package com.honya.platform.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class HonyaResilience {

    @Bean
    public CircuitBreakerRegistry honyaCircuitBreakerRegistry(
            @Value("${bookstore.resilience.circuit-breaker.sliding-window-size:10}") int slidingWindowSize,
            @Value("${bookstore.resilience.circuit-breaker.minimum-number-of-calls:5}") int minimumNumberOfCalls,
            @Value("${bookstore.resilience.circuit-breaker.failure-rate-threshold:50}") float failureRateThreshold,
            @Value("${bookstore.resilience.circuit-breaker.wait-duration-seconds:10}") long waitDurationSeconds,
            @Value("${bookstore.resilience.circuit-breaker.permitted-calls-in-half-open:3}") int permittedCallsInHalfOpen) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowSize(slidingWindowSize)
                .minimumNumberOfCalls(minimumNumberOfCalls)
                .failureRateThreshold(failureRateThreshold)
                .waitDurationInOpenState(Duration.ofSeconds(waitDurationSeconds))
                .permittedNumberOfCallsInHalfOpenState(permittedCallsInHalfOpen)
                .ignoreException(ResilientExceptions::isBusinessError)
                .build();
        return CircuitBreakerRegistry.of(config);
    }

    @Bean
    public RetryRegistry honyaRetryRegistry(
            @Value("${bookstore.resilience.retry.max-attempts:3}") int maxAttempts,
            @Value("${bookstore.resilience.retry.wait-duration-ms:200}") long waitDurationMs) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(maxAttempts)
                .waitDuration(Duration.ofMillis(waitDurationMs))
                .retryOnException(ResilientExceptions::isRetryable)
                .build();
        return RetryRegistry.of(config);
    }

    @Bean
    public ResilientCalls resilientCalls(CircuitBreakerRegistry circuitBreakers, RetryRegistry retries) {
        return new ResilientCalls(circuitBreakers, retries);
    }
}
