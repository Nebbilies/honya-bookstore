package com.honya.bookstore.checkout.application;

import com.honya.bookstore.checkout.domain.SagaInstance;
import com.honya.bookstore.checkout.domain.SagaStatus;
import com.honya.bookstore.checkout.infrastructure.persistence.SagaInstanceRepository;
import com.honya.bookstore.checkout.outbox.CheckoutOutboxWriter;
import com.honya.bookstore.shared.integration.cart.CartItemSnapshot;
import com.honya.bookstore.shared.integration.saga.command.CancelOrderCommand;
import com.honya.bookstore.shared.integration.saga.command.ReleaseStockCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SagaRecoveryService {

    private final SagaInstanceRepository sagaRepository;
    private final CheckoutOutboxWriter outboxWriter;
    private final ObjectMapper objectMapper;

    @Transactional
    public void confirmPayment(UUID orderId) {
        sagaRepository.findByOrderId(orderId).ifPresent(saga -> {
            if (saga.getStatus() == SagaStatus.AWAITING_PAYMENT) {
                saga.setStatus(SagaStatus.COMPLETED);
                saga.setUpdatedAt(OffsetDateTime.now());
                sagaRepository.save(saga);
            }
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
        saga.setStatus(SagaStatus.COMPENSATED);
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
