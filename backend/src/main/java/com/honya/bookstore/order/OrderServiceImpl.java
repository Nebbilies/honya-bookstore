package com.honya.bookstore.order;

import com.honya.bookstore.order.enums.OrderStatus;
import com.honya.bookstore.order.api.event.OrderItemEventDTO;
import com.honya.bookstore.order.api.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Order createOrder(String userId, Order orderDetails) {
        orderDetails.setUserId(UUID.fromString(userId));
        orderDetails.setStatus(OrderStatus.PENDING);

        if (orderDetails.getItems() != null) {
            orderDetails.getItems().forEach(item -> item.setOrder(orderDetails));
        }

        Order savedOrder = orderRepository.save(orderDetails);

        List<OrderItemEventDTO> eventItems = savedOrder.getItems().stream()
                .map(item -> new OrderItemEventDTO(item.getBookId(), item.getQuantity()))
                .collect(Collectors.toList());

        eventPublisher.publishEvent(new OrderPlacedEvent(
                savedOrder.getId(),
                UUID.fromString(userId),
                eventItems
        ));

        return savedOrder;
    }

    @Override
    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }

    @Override
    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(UUID.fromString(userId));
    }

    @Override
    @Transactional
    public Order updateOrderStatus(UUID orderId, String status) {
        Order order = getOrderById(orderId);
        order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        return orderRepository.save(order);
    }
}