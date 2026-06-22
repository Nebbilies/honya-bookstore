package com.honya.bookstore.catalog.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
public class MediaClient {

    private final RestClient restClient;

    public MediaClient(@Value("${media.base-url:http://localhost:8082}") String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(5));
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    public MediaView getMediaById(UUID id) {
        return restClient.get()
                .uri("/api/media/{id}", id)
                .headers(headers -> currentAuthorization().ifPresent(value -> headers.set(HttpHeaders.AUTHORIZATION, value)))
                .retrieve()
                .body(MediaView.class);
    }

    private Optional<String> currentAuthorization() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return Optional.ofNullable(attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION));
        }
        return Optional.empty();
    }
}
