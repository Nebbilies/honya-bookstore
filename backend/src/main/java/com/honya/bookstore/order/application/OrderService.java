package com.honya.bookstore.order.application;

import com.honya.bookstore.order.domain.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order createOrder(String userId, Order orderDetails);
    Order getOrderById(UUID orderId);
    List<Order> getOrdersByUserId(String userId);
    Order updateOrderStatus(UUID orderId, String status);
    Order updatePaymentUrl(UUID orderId, String paymentUrl);
    Order updatePaymentStatus(UUID orderId, boolean paid, String transactionNo, String status);
}