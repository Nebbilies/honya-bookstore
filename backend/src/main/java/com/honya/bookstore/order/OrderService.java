package com.honya.bookstore.order;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order createOrder(String userId, Order orderDetails);
    Order getOrderById(UUID orderId);
    List<Order> getOrdersByUserId(String userId);
    Order updateOrderStatus(UUID orderId, String status);
}