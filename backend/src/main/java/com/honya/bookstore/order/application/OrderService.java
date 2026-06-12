package com.honya.bookstore.order.application;

import com.honya.bookstore.order.domain.Order;
import com.honya.bookstore.order.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order createOrder(String userId, Order orderDetails);
    Order getOrderById(UUID orderId);
    Page<Order> searchOrders(OrderStatus status, String search, Pageable pageable);
    List<Order> getOrdersByUserId(String userId);
    Page<Order> getOrdersByUserId(String userId, Pageable pageable);
    Order updateOrderStatus(UUID orderId, String status);
    Order updatePaymentUrl(UUID orderId, String paymentUrl);
    Order updatePaymentStatus(UUID orderId, boolean paid, String transactionNo, String status);
}