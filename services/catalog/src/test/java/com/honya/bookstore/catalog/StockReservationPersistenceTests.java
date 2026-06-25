package com.honya.bookstore.catalog;

import com.honya.bookstore.catalog.application.BookService;
import com.honya.bookstore.catalog.domain.Book;
import com.honya.bookstore.catalog.infrastructure.persistence.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://issuer.example/realms/honya",
        "app.security.jwt.jwk-set-uri=https://issuer.example/realms/honya/protocol/openid-connect/certs",
        "app.security.jwt.audience=honya-api"
})
class StockReservationPersistenceTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3.13-management-alpine");

    @DynamicPropertySource
    static void flywayProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }

    @Autowired
    BookService bookService;

    @Autowired
    BookRepository bookRepository;

    private UUID persistBook(int stock) {
        return bookRepository.save(Book.builder()
                .title("Naruto")
                .author("Kishimoto")
                .price(1000)
                .stockQuantity(stock)
                .build()).getId();
    }

    private int stockOf(UUID bookId) {
        return bookRepository.findById(bookId).orElseThrow().getStockQuantity();
    }

    @Test
    void reserveDeductsExactlyOncePerSagaEvenIfRetried() {
        UUID bookId = persistBook(10);
        UUID sagaId = UUID.randomUUID();

        bookService.reserveStock(sagaId, bookId, 3);
        bookService.reserveStock(sagaId, bookId, 3);

        assertEquals(7, stockOf(bookId));
    }

    @Test
    void releaseRestoresExactlyOncePerSagaEvenIfRetried() {
        UUID bookId = persistBook(10);
        UUID sagaId = UUID.randomUUID();
        bookService.reserveStock(sagaId, bookId, 3);

        bookService.releaseStock(sagaId, bookId, 3);
        bookService.releaseStock(sagaId, bookId, 3);

        assertEquals(10, stockOf(bookId));
    }

    @Test
    void releaseWithoutReservationIsNoOp() {
        UUID bookId = persistBook(10);

        bookService.releaseStock(UUID.randomUUID(), bookId, 3);

        assertEquals(10, stockOf(bookId));
    }
}
