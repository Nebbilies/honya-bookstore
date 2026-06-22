package com.honya.bookstore.article.config;

import com.honya.platform.security.PublicEndpoint;
import com.honya.platform.security.PublicEndpointsContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ArticlePublicEndpoints {

    @Bean
    PublicEndpointsContributor articlePublicEndpoints() {
        return () -> List.of(PublicEndpoint.get("/api/articles/public", "/api/articles/public/**"));
    }
}
