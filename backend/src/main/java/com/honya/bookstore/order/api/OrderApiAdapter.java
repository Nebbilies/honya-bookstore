package com.honya.bookstore.order.api;

import com.honya.bookstore.order.application.OrderService;
import com.honya.bookstore.order.domain.Order;
import com.honya.bookstore.order.domain.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderApiAdapter implements OrderApi {

    private final OrderService orderService;

    @Override
    public OrderResponse createOrder(String userId, OrderRequest orderDetails) {
        return toResponse(orderService.createOrder(userId, toOrder(orderDetails)));
    }

    @Override
    public OrderResponse getOrderById(UUID orderId) {
        return toResponse(orderService.getOrderById(orderId));
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(String userId) {
        return orderService.getOrdersByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private Order toOrder(OrderRequest request) {
        return Order.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .address(request.address())
                .city(request.city())
                .items(request.items().stream()
                        .map(item -> OrderItem.builder()
                                .bookId(item.bookId())
                                .quantity(item.quantity())
                                .price(item.price())
                                .build())
                        .toList())
                .totalAmount(request.totalAmount())
                .build();
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getFirstName(),
                order.getLastName(),
                order.getAddress(),
                order.getCity(),
                order.getProvider() == null ? null : order.getProvider().name(),
                order.getStatus() == null ? null : order.getStatus().name(),
                order.getIsPaid(),
                order.getTotalAmount(),
                order.getUserId(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getId(),
                                item.getBookId(),
                                item.getQuantity(),
                                item.getPrice()
                        ))
                        .toList()
        );
    }
}
