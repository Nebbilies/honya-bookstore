package com.honya.bookstore.catalog;

import com.honya.bookstore.order.api.event.OrderPlacedEvent;
import com.honya.bookstore.catalog.service.BookService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final BookService bookService;

    @EventListener
    public void handleOrder(OrderPlacedEvent event) {

        event.getItems().forEach(item -> {
            bookService.reduceStock(item.getBookId(), item.getQuantity());
        });
    }
}