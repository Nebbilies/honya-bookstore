package com.honya.bookstore.catalog.infrastructure.persistence.config;

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

    // cause: when running inside docker, minio generate presign url using
    // internal url: minio:9000 --> public cannot reach --> separate client endpoints
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
