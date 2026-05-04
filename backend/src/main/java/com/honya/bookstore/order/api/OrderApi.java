package com.honya.bookstore.order.api;

import com.honya.bookstore.order.Order;

import java.util.List;
import java.util.UUID;

public interface OrderApi {
    Order createOrder(String userId, Order orderDetails);
    Order getOrderById(UUID orderId);
    List<Order> getOrdersByUserId(String userId);
}
