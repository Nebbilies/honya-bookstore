package com.honya.bookstore.media.infrastructure.persistence.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Value("${spring.minio.internal-url}")
    private String internalUrl;

    @Value("${spring.minio.public-url}")
    private String publicUrl;

    @Value("${spring.minio.access-key}")
    private String accessKey;

    @Value("${spring.minio.secret-key}")
    private String secretKey;

    @Bean("internalMinioClient")
    public MinioClient internalMinioClient() {
        return MinioClient.builder()
                .endpoint(internalUrl)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean("publicMinioClient")
    public MinioClient publicMinioClient() {
        return MinioClient.builder()
                .endpoint(publicUrl)
                .credentials(accessKey, secretKey)
                .build();
    }
}
