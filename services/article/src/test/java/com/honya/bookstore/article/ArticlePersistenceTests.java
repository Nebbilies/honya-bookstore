package com.honya.bookstore.article;

import com.honya.bookstore.article.domain.Article;
import com.honya.bookstore.article.domain.ArticleStatus;
import com.honya.bookstore.article.infrastructure.persistence.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

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
class ArticlePersistenceTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void flywayProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }

    @Autowired
    ArticleRepository articleRepository;

    @Test
    void flywayMigratesAndArticlePersistsWithTags() {
        Article saved = articleRepository.save(Article.builder()
                .slug("hello-world")
                .title("Hello World")
                .content("body")
                .tags(List.of("news", "release"))
                .status(ArticleStatus.PUBLISHED)
                .build());

        Article found = articleRepository.findById(saved.getId()).orElseThrow();
        assertEquals("hello-world", found.getSlug());
        assertEquals(List.of("news", "release"), found.getTags());
        assertTrue(articleRepository.findByStatus(ArticleStatus.PUBLISHED,
                org.springframework.data.domain.PageRequest.of(0, 10)).getTotalElements() >= 1);
    }
}
