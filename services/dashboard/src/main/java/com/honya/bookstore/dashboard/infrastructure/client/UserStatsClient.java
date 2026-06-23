package com.honya.bookstore.dashboard.infrastructure.client;

import com.honya.platform.resilience.ResilientCalls;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.Optional;

@Component
public class UserStatsClient {

    private final RestClient restClient;
    private final ResilientCalls resilientCalls;

    public UserStatsClient(@Value("${user.base-url:http://localhost:8085}") String baseUrl,
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

    public long totalUsers() {
        return resilientCalls.callOrDefault("user-totalUsers", true,
                () -> {
                    UserStatsView view = restClient.get()
                            .uri("/api/users/stats/total")
                            .headers(headers -> currentAuthorization().ifPresent(value -> headers.set(HttpHeaders.AUTHORIZATION, value)))
                            .retrieve()
                            .body(UserStatsView.class);
                    return view == null ? 0L : view.totalUsers();
                },
                () -> 0L);
    }

    private Optional<String> currentAuthorization() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return Optional.ofNullable(attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION));
        }
        return Optional.empty();
    }
}
