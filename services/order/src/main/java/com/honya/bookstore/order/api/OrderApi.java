package com.honya.bookstore.order.api;

import java.util.List;
import java.util.UUID;

public interface OrderApi {
    OrderResponse createOrder(String userId, OrderRequest orderDetails);
    OrderResponse getOrderById(UUID orderId);
    List<OrderResponse> getOrdersByUserId(String userId);
}
