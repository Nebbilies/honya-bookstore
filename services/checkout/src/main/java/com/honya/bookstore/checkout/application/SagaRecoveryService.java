package com.honya.bookstore.checkout.application;

import com.honya.bookstore.checkout.domain.SagaInstance;
import com.honya.bookstore.checkout.domain.SagaStatus;
import com.honya.bookstore.checkout.infrastructure.persistence.SagaInstanceRepository;
import com.honya.bookstore.checkout.outbox.CheckoutOutboxWriter;
import com.honya.bookstore.shared.integration.cart.CartItemSnapshot;
import com.honya.bookstore.shared.integration.saga.command.CancelOrderCommand;
import com.honya.bookstore.shared.integration.saga.command.ReleaseStockCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class SagaRecoveryService {

    private final SagaInstanceRepository sagaRepository;
    private final CheckoutOutboxWriter outboxWriter;
    private final ObjectMapper objectMapper;
    private final long paymentTimeoutMinutes;
    private final long maxLifetimeMinutes;

    public SagaRecoveryService(SagaInstanceRepository sagaRepository,
                               CheckoutOutboxWriter outboxWriter,
                               ObjectMapper objectMapper,
                               @Value("${bookstore.saga.payment-timeout-minutes:15}") long paymentTimeoutMinutes,
                               @Value("${bookstore.saga.max-lifetime-minutes:60}") long maxLifetimeMinutes) {
        this.sagaRepository = sagaRepository;
        this.outboxWriter = outboxWriter;
        this.objectMapper = objectMapper;
        this.paymentTimeoutMinutes = paymentTimeoutMinutes;
        this.maxLifetimeMinutes = maxLifetimeMinutes;
    }

    @Transactional
    public void confirmPayment(UUID orderId) {
        sagaRepository.findByOrderId(orderId).ifPresent(saga -> {
            switch (saga.getStatus()) {
                case AWAITING_PAYMENT -> transition(saga, SagaStatus.COMPLETED);
                case COMPENSATED -> {
                    log.warn("Payment confirmed for an already-compensated saga {} (order {}); flagging for refund",
                            saga.getId(), orderId);
                    transition(saga, SagaStatus.REFUND_REQUIRED);
                }
                default -> {
                    // COMPLETED or REFUND_REQUIRED: duplicate confirmation, nothing to do.
                }
            }
        });
    }

    @Transactional
    public void extendPaymentWindow(UUID orderId) {
        sagaRepository.findByOrderId(orderId).ifPresent(saga -> {
            if (saga.getStatus() != SagaStatus.AWAITING_PAYMENT) {
                return;
            }
            OffsetDateTime cap = saga.getCreatedAt().plusMinutes(maxLifetimeMinutes);
            OffsetDateTime candidate = OffsetDateTime.now().plusMinutes(paymentTimeoutMinutes);
            saga.setExpiresAt(candidate.isBefore(cap) ? candidate : cap);
            saga.setUpdatedAt(OffsetDateTime.now());
            sagaRepository.save(saga);
        });
    }

    @Transactional
    public void compensateExpired(OffsetDateTime now) {
        sagaRepository.findByStatusAndExpiresAtBefore(SagaStatus.AWAITING_PAYMENT, now)
                .forEach(this::compensate);
    }

    private void compensate(SagaInstance saga) {
        for (CartItemSnapshot line : deserialize(saga.getLines())) {
            outboxWriter.enqueue("RELEASE_STOCK", saga.getId(),
                    new ReleaseStockCommand(saga.getId(), line.bookId(), line.quantity()));
        }
        if (saga.getOrderId() != null) {
            outboxWriter.enqueue("CANCEL_ORDER", saga.getId(),
                    new CancelOrderCommand(saga.getId(), saga.getOrderId()));
        }
        transition(saga, SagaStatus.COMPENSATED);
    }

    private void transition(SagaInstance saga, SagaStatus status) {
        saga.setStatus(status);
        saga.setUpdatedAt(OffsetDateTime.now());
        sagaRepository.save(saga);
    }

    private List<CartItemSnapshot> deserialize(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<CartItemSnapshot>>() {
            });
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to read saga lines", ex);
        }
    }
}
