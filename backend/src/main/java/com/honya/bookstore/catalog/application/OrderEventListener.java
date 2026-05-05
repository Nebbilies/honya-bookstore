package com.honya.bookstore.catalog.application;

import com.honya.bookstore.catalog.domain.CatalogProcessedOrderEvent;
import com.honya.bookstore.catalog.infrastructure.persistence.CatalogProcessedOrderEventRepository;
import com.honya.bookstore.order.api.event.OrderPlacedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final BookService bookService;
    private final CatalogProcessedOrderEventRepository processedOrderEventRepository;

    @EventListener
    @Transactional
    public void handleOrder(OrderPlacedEvent event) {
        if (processedOrderEventRepository.existsByOrderId(event.getOrderId())) {
            return;
        }

        processedOrderEventRepository.save(CatalogProcessedOrderEvent.builder()
                .orderId(event.getOrderId())
                .processedAt(OffsetDateTime.now())
                .build());

        event.getItems().forEach(item -> {
            bookService.reduceStock(item.getBookId(), item.getQuantity());
        });
    }
}