package com.honya.bookstore.order.config;

import com.honya.platform.security.PublicEndpoint;
import com.honya.platform.security.PublicEndpointsContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OrderPublicEndpoints {

    @Bean
    PublicEndpointsContributor publicEndpoints() {
        return () -> List.of(
                PublicEndpoint.get("/api/orders/payment/vnpay/ipn", "/api/orders/payment/vnpay/return")
        );
    }
}
