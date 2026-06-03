package com.honya.bookstore.order.application;

import com.honya.bookstore.order.domain.Order;
import com.honya.bookstore.order.domain.OrderStatus;

import java.time.OffsetDateTime;
import com.honya.bookstore.order.infrastructure.persistence.OrderRepository;
import com.honya.bookstore.shared.error.InvalidOrderStatusException;
import com.honya.bookstore.shared.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public Order createOrder(String userId, Order orderDetails) {
        OffsetDateTime now = OffsetDateTime.now();
        orderDetails.setStatus(orderDetails.getStatus() == null ? OrderStatus.PENDING : orderDetails.getStatus());
        orderDetails.setIsPaid(orderDetails.getIsPaid() == null ? Boolean.FALSE : orderDetails.getIsPaid());
        orderDetails.setCreatedAt(orderDetails.getCreatedAt() == null ? now : orderDetails.getCreatedAt());
        orderDetails.setUpdatedAt(now);

        // Aggregate assigns its id, links items, and registers OrderPlacedDomainEvent.
        // Spring Data publishes it during save(); OrderPlacedDomainEventListener relays
        // it to the outbox in this same transaction.
        orderDetails.place(UUID.fromString(userId));

        return orderRepository.save(orderDetails);
    }

    @Override
    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    }

    @Override
    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(UUID.fromString(userId));
    }

    @Override
    @Transactional
    public Order updateOrderStatus(UUID orderId, String status) {
        Order order = getOrderById(orderId);
        try {
            order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException ex) {
            throw new InvalidOrderStatusException(status);
        }
        order.setUpdatedAt(OffsetDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updatePaymentUrl(UUID orderId, String paymentUrl) {
        Order order = getOrderById(orderId);
        order.setPaymentUrl(paymentUrl);
        order.setUpdatedAt(OffsetDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updatePaymentStatus(UUID orderId, boolean paid, String transactionNo, String status) {
        Order order = getOrderById(orderId);
        order.setIsPaid(paid);
        order.setPaymentTransactionNo(transactionNo);
        if (status != null) {
            try {
                order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new InvalidOrderStatusException(status);
            }
        }
        if (paid) {
            order.setPaidAt(OffsetDateTime.now());
        }
        order.setUpdatedAt(OffsetDateTime.now());
        return orderRepository.save(order);
    }
}