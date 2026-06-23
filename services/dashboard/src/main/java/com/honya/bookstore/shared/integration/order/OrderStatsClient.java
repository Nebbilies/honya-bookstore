package com.honya.bookstore.shared.integration.order;

import com.honya.platform.resilience.ResilientCalls;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Component
public class OrderStatsClient {

    private static final String BREAKER = "order-stats";

    private final RestClient restClient;
    private final ResilientCalls resilientCalls;

    public OrderStatsClient(@Value("${order.base-url:http://localhost:8088}") String baseUrl,
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

    public long salesThisMonth() {
        return getLong("/api/orders/stats/sales-this-month");
    }

    public long ordersThisMonth() {
        return getLong("/api/orders/stats/orders-this-month");
    }

    public long newCustomersThisMonth() {
        return getLong("/api/orders/stats/new-customers-this-month");
    }

    public List<MonthlyPoint> revenuePerYear(int year) {
        return resilientCalls.callOrDefault(BREAKER, true,
                () -> restClient.get()
                        .uri(uri -> uri.path("/api/orders/stats/revenue-per-year").queryParam("year", year).build())
                        .headers(this::relayAuth)
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<MonthlyPoint>>() {}),
                List::of);
    }

    public List<MonthlyPoint> ordersPerYear(int year) {
        return resilientCalls.callOrDefault(BREAKER, true,
                () -> restClient.get()
                        .uri(uri -> uri.path("/api/orders/stats/orders-per-year").queryParam("year", year).build())
                        .headers(this::relayAuth)
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<MonthlyPoint>>() {}),
                List::of);
    }

    public List<BestSellerStat> bestSellers(StatsPeriod period, int limit) {
        return resilientCalls.callOrDefault(BREAKER, true,
                () -> restClient.get()
                        .uri(uri -> uri.path("/api/orders/stats/best-sellers")
                                .queryParam("period", period)
                                .queryParam("limit", limit)
                                .build())
                        .headers(this::relayAuth)
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<BestSellerStat>>() {}),
                List::of);
    }

    public List<RecentOrderStat> recentOrders(int limit) {
        return resilientCalls.callOrDefault(BREAKER, true,
                () -> restClient.get()
                        .uri(uri -> uri.path("/api/orders/stats/recent-orders").queryParam("limit", limit).build())
                        .headers(this::relayAuth)
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<RecentOrderStat>>() {}),
                List::of);
    }

    private long getLong(String path) {
        return resilientCalls.callOrDefault(BREAKER, true,
                () -> {
                    Long value = restClient.get()
                            .uri(path)
                            .headers(this::relayAuth)
                            .retrieve()
                            .body(Long.class);
                    return value == null ? 0L : value;
                },
                () -> 0L);
    }

    private void relayAuth(HttpHeaders headers) {
        currentAuthorization().ifPresent(value -> headers.set(HttpHeaders.AUTHORIZATION, value));
    }

    private Optional<String> currentAuthorization() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return Optional.ofNullable(attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION));
        }
        return Optional.empty();
    }
}
