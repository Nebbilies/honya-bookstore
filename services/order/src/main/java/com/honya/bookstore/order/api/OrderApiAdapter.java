package com.honya.bookstore.order.api;

import com.honya.bookstore.order.application.OrderService;
import com.honya.bookstore.order.domain.Order;
import com.honya.bookstore.order.domain.OrderItem;
import com.honya.bookstore.order.domain.OrderItemBook;
import com.honya.bookstore.order.domain.OrderProvider;
import com.honya.bookstore.order.domain.OrderStatus;
import com.honya.bookstore.order.infrastructure.payment.VnPayUrlBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderApiAdapter implements OrderApi {

    private static final String INTERNAL_CLIENT_IP = "127.0.0.1";

    private final OrderService orderService;
    private final VnPayUrlBuilder vnPayUrlBuilder;

    @Override
    public OrderResponse createOrder(String userId, OrderRequest orderDetails) {
        Order created = orderService.createOrder(userId, toOrder(orderDetails));
        if (created.getProvider() == OrderProvider.VNPAY) {
            String paymentUrl = vnPayUrlBuilder.buildPaymentUrl(created, INTERNAL_CLIENT_IP, orderDetails.returnUrl());
            created = orderService.updatePaymentUrl(created.getId(), paymentUrl);
        }
        return toResponse(created);
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
        OrderProvider provider = request.provider() == null ? null : OrderProvider.valueOf(request.provider());
        return Order.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .address(request.address())
                .city(request.city())
                .email(request.email())
                .phone(request.phone())
                .provider(provider)
                .status(provider == OrderProvider.COD ? OrderStatus.PROCESSING : OrderStatus.PENDING)
                .items(request.items().stream()
                        .map(item -> OrderItem.builder()
                                .book(OrderItemBook.builder().id(item.bookId()).build())
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
                order.getEmail(),
                order.getPhone(),
                order.getPaymentUrl(),
                order.getProvider() == null ? null : order.getProvider().name(),
                order.getStatus() == null ? null : order.getStatus().name(),
                order.getIsPaid(),
                order.getTotalAmount(),
                order.getUserId(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getId(),
                                item.getBook() == null ? null : item.getBook().getId(),
                                item.getQuantity(),
                                item.getPrice()
                        ))
                        .toList()
        );
    }
}
