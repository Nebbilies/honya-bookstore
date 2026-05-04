package com.honya.bookstore.order.api;

import com.honya.bookstore.order.Order;
import com.honya.bookstore.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderApiAdapter implements OrderApi {

    private final OrderService orderService;

    @Override
    public Order createOrder(String userId, Order orderDetails) {
        return orderService.createOrder(userId, orderDetails);
    }

    @Override
    public Order getOrderById(UUID orderId) {
        return orderService.getOrderById(orderId);
    }

    @Override
    public List<Order> getOrdersByUserId(String userId) {
        return orderService.getOrdersByUserId(userId);
    }
}
