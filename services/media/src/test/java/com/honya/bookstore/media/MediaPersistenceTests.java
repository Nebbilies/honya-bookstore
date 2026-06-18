package com.honya.bookstore.media;

import com.honya.bookstore.media.domain.Media;
import com.honya.bookstore.media.infrastructure.persistence.MediaRepository;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.minio.internal-url=http://localhost:9000",
        "spring.minio.public-url=http://localhost:9000",
        "spring.minio.access-key=test",
        "spring.minio.secret-key=test",
        "spring.minio.media-bucket-name=media",
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://issuer.example/realms/honya",
        "app.security.jwt.jwk-set-uri=https://issuer.example/realms/honya/protocol/openid-connect/certs",
        "app.security.jwt.audience=honya-api"
})
class MediaPersistenceTests {

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
    MediaRepository mediaRepository;

    @Test
    void flywayMigratesAndMediaPersists() {
        Media saved = mediaRepository.save(Media.builder()
                .url("http://localhost:9000/media/x")
                .altText("x")
                .key("images/x")
                .order(0)
                .createdAt(OffsetDateTime.now())
                .build());

        assertTrue(mediaRepository.findById(saved.getId()).isPresent());
        assertFalse(mediaRepository.findByDeletedAtIsNull().isEmpty());
    }
}
