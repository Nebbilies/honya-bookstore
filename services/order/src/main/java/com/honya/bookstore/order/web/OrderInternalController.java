package com.honya.bookstore.order.web;

import com.honya.bookstore.order.api.OrderApi;
import com.honya.bookstore.order.api.OrderRequest;
import com.honya.bookstore.order.api.OrderResponse;
import com.honya.bookstore.security.CustomerOnly;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders/internal")
@RequiredArgsConstructor
@CustomerOnly
public class OrderInternalController {

    private final OrderApi orderApi;

    @PostMapping
    public OrderResponse createOrder(@AuthenticationPrincipal Jwt jwt, @RequestBody OrderRequest request) {
        return orderApi.createOrder(jwt.getSubject(), request);
    }
}
