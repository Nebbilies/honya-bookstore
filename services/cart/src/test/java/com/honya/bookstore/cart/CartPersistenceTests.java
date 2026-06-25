package com.honya.bookstore.cart;

import com.honya.bookstore.cart.domain.Cart;
import com.honya.bookstore.cart.domain.CartItem;
import com.honya.bookstore.cart.infrastructure.persistence.CartRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
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
@Transactional
class CartPersistenceTests {

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
    CartRepository cartRepository;

    @Test
    void flywayMigratesAndCartPersistsWithItem() {
        UUID ownerId = UUID.randomUUID();
        Cart cart = Cart.builder()
                .ownerId(ownerId)
                .updatedAt(OffsetDateTime.now())
                .items(new ArrayList<>())
                .build();

        CartItem item = CartItem.builder()
                .cart(cart)
                .catalogItemId(UUID.randomUUID())
                .title("Naruto")
                .author("Kishimoto")
                .imageUrl("/img.png")
                .unitPrice(1000)
                .quantity(2)
                .build();
        cart.getItems().add(item);

        Cart saved = cartRepository.save(cart);

        assertTrue(cartRepository.findById(saved.getId()).isPresent());
        assertEquals(1L, cartRepository.count());
        assertEquals(1, cartRepository.findById(saved.getId()).get().getItems().size());
    }
}
