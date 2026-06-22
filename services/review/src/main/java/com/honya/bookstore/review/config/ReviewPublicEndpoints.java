package com.honya.bookstore.review.config;

import com.honya.platform.security.PublicEndpoint;
import com.honya.platform.security.PublicEndpointsContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ReviewPublicEndpoints {

    @Bean
    PublicEndpointsContributor reviewPublicEndpoints() {
        return () -> List.of(PublicEndpoint.get("/api/reviews", "/api/reviews/**"));
    }
}
