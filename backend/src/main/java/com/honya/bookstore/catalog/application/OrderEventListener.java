package com.honya.bookstore.catalog.application;

import tools.jackson.databind.ObjectMapper;
import com.honya.bookstore.catalog.domain.CatalogProcessedOrderEvent;
import com.honya.bookstore.catalog.infrastructure.persistence.CatalogProcessedOrderEventRepository;
import com.honya.bookstore.shared.integration.order.RabbitOrderIntegrationConfig;
import com.honya.bookstore.shared.integration.order.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Component("catalogOrderEventListener")
@RequiredArgsConstructor
public class OrderEventListener {

    private final BookService bookService;
    private final CatalogProcessedOrderEventRepository processedOrderEventRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = RabbitOrderIntegrationConfig.CATALOG_QUEUE)
    @Transactional
    public void handleOrder(String payload) {
        OrderPlacedEvent event = deserialize(payload);

        if (processedOrderEventRepository.existsByOrderId(event.orderId())) {
            return;
        }

        processedOrderEventRepository.save(CatalogProcessedOrderEvent.builder()
                .orderId(event.orderId())
                .processedAt(OffsetDateTime.now())
                .build());

        event.items().forEach(item -> {
            bookService.reduceStock(item.bookId(), item.quantity());
        });
    }

    private OrderPlacedEvent deserialize(String payload) {
        try {
            return objectMapper.readValue(payload, OrderPlacedEvent.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to handle order integration event", ex);
        }
    }
}
