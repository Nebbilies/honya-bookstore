package com.honya.platform.resilience;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

public final class ResilientExceptions {

    private ResilientExceptions() {
    }

    public static boolean isBusinessError(Throwable throwable) {
        if (throwable instanceof HttpClientErrorException) {
            return true;
        }
        if (throwable instanceof ResponseStatusException statusException) {
            return statusException.getStatusCode().is4xxClientError();
        }
        return false;
    }

    public static boolean isRetryable(Throwable throwable) {
        if (throwable instanceof CallNotPermittedException) {
            return false;
        }
        return !isBusinessError(throwable);
    }
}
