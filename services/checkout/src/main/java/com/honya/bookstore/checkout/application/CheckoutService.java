package com.honya.bookstore.checkout.application;

import com.honya.bookstore.checkout.domain.SagaInstance;
import com.honya.bookstore.checkout.domain.SagaStatus;
import com.honya.bookstore.checkout.infrastructure.persistence.SagaInstanceRepository;
import com.honya.bookstore.shared.integration.cart.CartClient;
import com.honya.bookstore.shared.integration.cart.CartItemSnapshot;
import com.honya.bookstore.shared.integration.catalog.CatalogClient;
import com.honya.bookstore.shared.integration.order.OrderClient;
import com.honya.bookstore.shared.integration.order.OrderItemRequest;
import com.honya.bookstore.shared.integration.order.OrderRequest;
import com.honya.bookstore.shared.integration.order.OrderResponse;
import com.honya.bookstore.checkout.web.dto.CheckoutRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CheckoutService {

    private static final String VNPAY = "VNPAY";

    private final CartClient cartClient;
    private final CatalogClient catalogClient;
    private final OrderClient orderClient;
    private final SagaInstanceRepository sagaRepository;
    private final ObjectMapper objectMapper;
    private final long paymentTimeoutMinutes;

    public CheckoutService(CartClient cartClient,
                           CatalogClient catalogClient,
                           OrderClient orderClient,
                           SagaInstanceRepository sagaRepository,
                           ObjectMapper objectMapper,
                           @Value("${bookstore.saga.payment-timeout-minutes:15}") long paymentTimeoutMinutes) {
        this.cartClient = cartClient;
        this.catalogClient = catalogClient;
        this.orderClient = orderClient;
        this.sagaRepository = sagaRepository;
        this.objectMapper = objectMapper;
        this.paymentTimeoutMinutes = paymentTimeoutMinutes;
    }

    public OrderResponse checkout(String userId, CheckoutRequestDTO request) {
        List<CartItemSnapshot> lines = cartClient.getCheckoutSnapshot(userId).items();
        List<OrderItemRequest> items = lines.stream()
                .map(line -> new OrderItemRequest(line.bookId(), line.quantity(), catalogClient.getBook(line.bookId()).price()))
                .toList();

        OffsetDateTime now = OffsetDateTime.now();
        SagaInstance saga = sagaRepository.save(SagaInstance.builder()
                .userId(UUID.fromString(userId))
                .provider(request.getProvider())
                .status(SagaStatus.STARTED)
                .lines(serialize(lines))
                .createdAt(now)
                .updatedAt(now)
                .build());

        List<CartItemSnapshot> reserved = new ArrayList<>();
        for (CartItemSnapshot line : lines) {
            try {
                catalogClient.reserve(saga.getId(), line.bookId(), line.quantity());
                reserved.add(line);
            } catch (RuntimeException ex) {
                compensate(saga, reserved);
                throw ex;
            }
        }
        transition(saga, SagaStatus.STOCK_RESERVED);

        OrderResponse order;
        try {
            order = orderClient.createOrder(userId, new OrderRequest(
                    request.getFirstName(),
                    request.getLastName(),
                    request.getAddress(),
                    request.getCity(),
                    request.getEmail(),
                    request.getPhone(),
                    request.getProvider(),
                    request.getReturnUrl(),
                    items,
                    items.stream().mapToInt(item -> item.price() * item.quantity()).sum()));
        } catch (RuntimeException ex) {
            compensate(saga, reserved);
            throw ex;
        }
        saga.setOrderId(order.id());
        transition(saga, SagaStatus.ORDER_CREATED);

        if (VNPAY.equalsIgnoreCase(request.getProvider())) {
            saga.setExpiresAt(OffsetDateTime.now().plusMinutes(paymentTimeoutMinutes));
            transition(saga, SagaStatus.AWAITING_PAYMENT);
        } else {
            transition(saga, SagaStatus.COMPLETED);
        }

        return order;
    }

    private void compensate(SagaInstance saga, List<CartItemSnapshot> reserved) {
        reserved.forEach(line -> catalogClient.release(saga.getId(), line.bookId(), line.quantity()));
        transition(saga, SagaStatus.COMPENSATED);
    }

    private void transition(SagaInstance saga, SagaStatus status) {
        saga.setStatus(status);
        saga.setUpdatedAt(OffsetDateTime.now());
        sagaRepository.save(saga);
    }

    private String serialize(List<CartItemSnapshot> lines) {
        try {
            return objectMapper.writeValueAsString(lines);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize saga lines", ex);
        }
    }
}
