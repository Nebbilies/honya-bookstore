package com.honya.bookstore.catalog.config;

import com.honya.platform.security.PublicEndpoint;
import com.honya.platform.security.PublicEndpointsContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CatalogPublicEndpoints {

    @Bean
    PublicEndpointsContributor publicEndpoints() {
        return () -> List.of(
                PublicEndpoint.get("/api/books", "/api/books/**"),
                PublicEndpoint.get("/api/categories", "/api/categories/**")
        );
    }
}
