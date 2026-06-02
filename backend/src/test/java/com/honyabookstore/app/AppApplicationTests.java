package com.honya.bookstore.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = com.honya.bookstore.BookstoreApplication.class)
@Testcontainers
// Self-contained smoke test: supplies the external properties the context requires
// (minio + oauth) with throwaway values, and validates the Flyway-migrated schema.
// Datasource + RabbitMQ are wired from the containers below via @ServiceConnection.
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
class AppApplicationTests {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

	@Container
	@ServiceConnection
	static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3.13-management-alpine");

	// Flyway has its own explicit url in application.yaml; point it at the container too,
	// otherwise it would still target localhost. (datasource is wired via @ServiceConnection.)
	@DynamicPropertySource
	static void flywayProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.flyway.url", postgres::getJdbcUrl);
		registry.add("spring.flyway.user", postgres::getUsername);
		registry.add("spring.flyway.password", postgres::getPassword);
	}

	@Test
	void contextLoads() {
	}

}
