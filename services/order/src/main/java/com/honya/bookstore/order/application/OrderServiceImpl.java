package com.honya.bookstore.order.application;

import com.honya.bookstore.order.domain.Order;
import com.honya.bookstore.order.domain.OrderItem;
import com.honya.bookstore.order.domain.OrderItemBook;
import com.honya.bookstore.order.domain.OrderProvider;
import com.honya.bookstore.order.domain.OrderStatus;

import java.time.OffsetDateTime;
import com.honya.bookstore.order.infrastructure.persistence.OrderItemBookRepository;
import com.honya.bookstore.order.infrastructure.persistence.OrderRepository;
import com.honya.bookstore.order.infrastructure.persistence.OrderSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private final OrderItemBookRepository orderItemBookRepository;

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

        if (orderDetails.getItems() != null) {
            orderDetails.getItems().stream()
                    .filter(item -> item.getBook() != null && item.getBook().getId() != null)
                    .forEach(item -> item.setBook(orderItemBookRepository.save(item.getBook())));
        }

        if (orderDetails.getProvider() == OrderProvider.COD) {
            orderDetails.confirm();
        }

        return orderRepository.save(orderDetails);
    }

    @Override
    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    }

    @Override
    public Page<Order> searchOrders(OrderStatus status, String search, Pageable pageable) {
        Specification<Order> spec = Specification
                .where(OrderSpecifications.hasStatus(status))
                .and(OrderSpecifications.recipientContains(search));
        return orderRepository.findAll(spec, pageable);
    }

    @Override
    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(UUID.fromString(userId));
    }

    @Override
    public Page<Order> getOrdersByUserId(String userId, Pageable pageable) {
        return orderRepository.findByUserId(UUID.fromString(userId), pageable);
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
        boolean cancelled = order.getStatus() == OrderStatus.CANCELLED;
        order.setIsPaid(paid);
        order.setPaymentTransactionNo(transactionNo);
        if (status != null && !cancelled) {
            try {
                order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new InvalidOrderStatusException(status);
            }
        }
        if (paid) {
            order.setPaidAt(OffsetDateTime.now());
            // A payment landing on an already-cancelled order (timed-out saga) must not
            // resurrect it: record the payment and emit PaymentConfirmed so the saga can
            // flag a refund, but do not place the order (no OrderPlaced) or change its status.
            if (!cancelled) {
                order.confirm();
            }
            order.markPaymentConfirmed(transactionNo);
        }
        order.setUpdatedAt(OffsetDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order recordPaymentFailure(UUID orderId, String reason) {
        Order order = getOrderById(orderId);
        order.markPaymentFailed(reason);
        order.setUpdatedAt(OffsetDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order recordPaymentRetry(UUID orderId) {
        Order order = getOrderById(orderId);
        order.markPaymentRetried();
        order.setUpdatedAt(OffsetDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order cancelOrder(UUID orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() != OrderStatus.CANCELLED) {
            order.setStatus(OrderStatus.CANCELLED);
            order.setUpdatedAt(OffsetDateTime.now());
            orderRepository.save(order);
        }
        return order;
    }
}