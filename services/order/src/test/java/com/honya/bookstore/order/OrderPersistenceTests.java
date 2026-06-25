package com.honya.bookstore.order;

import com.honya.bookstore.order.domain.Order;
import com.honya.bookstore.order.domain.OrderItem;
import com.honya.bookstore.order.domain.OrderItemBook;
import com.honya.bookstore.order.domain.OrderProvider;
import com.honya.bookstore.order.domain.OrderStatus;
import com.honya.bookstore.order.infrastructure.persistence.OrderItemBookRepository;
import com.honya.bookstore.order.infrastructure.persistence.OrderRepository;
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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://issuer.example/realms/honya",
        "app.security.jwt.jwk-set-uri=https://issuer.example/realms/honya/protocol/openid-connect/certs",
        "app.security.jwt.audience=honya-api"
})
class OrderPersistenceTests {

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
    OrderRepository orderRepository;

    @Autowired
    OrderItemBookRepository orderItemBookRepository;

    @Test
    void flywayMigratesAndOrderPersistsWithItemAndSnapshot() {
        OrderItemBook book = orderItemBookRepository.save(OrderItemBook.builder()
                .id(UUID.randomUUID())
                .title("Naruto")
                .author("Kishimoto")
                .price(1000)
                .rating(5)
                .build());

        Order order = Order.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .status(OrderStatus.PENDING)
                .provider(OrderProvider.COD)
                .isPaid(false)
                .totalAmount(2000)
                .createdAt(OffsetDateTime.now())
                .items(new ArrayList<>())
                .build();

        OrderItem item = OrderItem.builder()
                .order(order)
                .book(book)
                .quantity(2)
                .price(1000)
                .build();
        order.getItems().add(item);

        Order saved = orderRepository.save(order);

        assertTrue(orderRepository.findById(saved.getId()).isPresent());
        assertEquals(1L, orderRepository.count());
        assertEquals(1, orderRepository.findById(saved.getId()).get().getItems().size());
    }
}
