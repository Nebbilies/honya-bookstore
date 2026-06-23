package com.honya.bookstore.shared.integration.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.Optional;

@Component
public class OrderClient {

    private final RestClient restClient;

    public OrderClient(@Value("${order.base-url:http://localhost:8088}") String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(5));
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    public OrderResponse createOrder(String userId, OrderRequest request) {
        return restClient.post()
                .uri("/api/orders/internal")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> {
                    currentAuthorization().ifPresent(value -> headers.set(HttpHeaders.AUTHORIZATION, value));
                    headers.set("X-User-Id", userId);
                })
                .body(request)
                .retrieve()
                .body(OrderResponse.class);
    }

    private Optional<String> currentAuthorization() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return Optional.ofNullable(attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION));
        }
        return Optional.empty();
    }
}
