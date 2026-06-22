package com.honya.platform.security;

import org.springframework.http.HttpMethod;

public record PublicEndpoint(HttpMethod method, String[] patterns) {

    public static PublicEndpoint get(String... patterns) {
        return new PublicEndpoint(HttpMethod.GET, patterns);
    }

    public static PublicEndpoint anyMethod(String... patterns) {
        return new PublicEndpoint(null, patterns);
    }
}
