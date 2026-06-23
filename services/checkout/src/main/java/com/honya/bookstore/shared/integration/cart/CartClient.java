package com.honya.bookstore.shared.integration.cart;

import com.honya.platform.resilience.ResilientCalls;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CartClient {

    private final RestClient restClient;
    private final ResilientCalls resilientCalls;

    public CartClient(@Value("${cart.base-url:http://localhost:8087}") String baseUrl,
                      ResilientCalls resilientCalls) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(5));
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
        this.resilientCalls = resilientCalls;
    }

    public CartSnapshot getCheckoutSnapshot(String userId) {
        CartResponse cart = resilientCalls.call("cart-getCheckoutSnapshot", true, () -> restClient.get()
                .uri("/api/cart")
                .headers(headers -> {
                    currentAuthorization().ifPresent(value -> headers.set(HttpHeaders.AUTHORIZATION, value));
                    headers.set("X-User-Id", userId);
                })
                .retrieve()
                .body(CartResponse.class));

        List<CartItemSnapshot> items = cart == null || cart.items() == null
                ? List.of()
                : cart.items().stream()
                        .map(item -> new CartItemSnapshot(item.book().id(), item.quantity()))
                        .collect(Collectors.toList());

        UUID ownerId = cart == null ? null : cart.ownerId();
        return new CartSnapshot(ownerId, items);
    }

    private Optional<String> currentAuthorization() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return Optional.ofNullable(attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION));
        }
        return Optional.empty();
    }

    private record CartResponse(UUID ownerId, List<CartItem> items) {
    }

    private record CartItem(BookRef book, Integer quantity) {
    }

    private record BookRef(UUID id) {
    }
}
