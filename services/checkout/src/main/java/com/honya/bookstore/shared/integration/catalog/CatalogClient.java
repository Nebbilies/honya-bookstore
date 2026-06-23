package com.honya.bookstore.shared.integration.catalog;

import com.honya.platform.resilience.ResilientCalls;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CatalogClient {

    private static final String FALLBACK_COVER = "/images/fallbackBookImage.png";

    private final RestClient restClient;
    private final ResilientCalls resilientCalls;

    public CatalogClient(@Value("${catalog.base-url:http://localhost:8086}") String baseUrl,
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

    public CatalogBookView getBook(UUID bookId) {
        return resilientCalls.call("catalog-getBook", true, () -> {
            CatalogBookResponse book = restClient.get()
                    .uri("/api/books/{id}", bookId)
                    .headers(headers -> currentAuthorization().ifPresent(value -> headers.set(HttpHeaders.AUTHORIZATION, value)))
                    .retrieve()
                    .body(CatalogBookResponse.class);

            return new CatalogBookView(
                    book.id(),
                    book.title(),
                    book.author() == null ? "Unknown" : book.author(),
                    coverUrl(book),
                    book.price()
            );
        });
    }

    private static String coverUrl(CatalogBookResponse book) {
        if (book.media() == null || book.media().isEmpty()) {
            return FALLBACK_COVER;
        }
        return book.media().stream()
                .filter(media -> Boolean.TRUE.equals(media.isCover()))
                .map(CatalogMedia::url)
                .findFirst()
                .orElseGet(() -> book.media().stream()
                        .map(CatalogMedia::url)
                        .findFirst()
                        .orElse(FALLBACK_COVER));
    }

    public void reserve(UUID sagaId, UUID bookId, Integer quantity) {
        resilientCalls.run("catalog-reserve", true, () -> {
            try {
                restClient.post()
                        .uri("/api/books/{id}/reserve", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers -> currentAuthorization().ifPresent(value -> headers.set(HttpHeaders.AUTHORIZATION, value)))
                        .body(new StockCommand(sagaId, quantity))
                        .retrieve()
                        .toBodilessEntity();
            } catch (HttpClientErrorException ex) {
                if (ex.getStatusCode().value() == HttpStatus.CONFLICT.value()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock");
                }
                throw ex;
            }
        });
    }

    public void release(UUID sagaId, UUID bookId, Integer quantity) {
        resilientCalls.run("catalog-release", true, () ->
                restClient.post()
                        .uri("/api/books/{id}/release", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers -> currentAuthorization().ifPresent(value -> headers.set(HttpHeaders.AUTHORIZATION, value)))
                        .body(new StockCommand(sagaId, quantity))
                        .retrieve()
                        .toBodilessEntity());
    }

    private Optional<String> currentAuthorization() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return Optional.ofNullable(attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION));
        }
        return Optional.empty();
    }

    private record StockCommand(UUID sagaId, Integer quantity) {
    }

    private record CatalogBookResponse(UUID id, String title, String author, Integer price, List<CatalogMedia> media) {
    }

    private record CatalogMedia(Boolean isCover, String url) {
    }
}
