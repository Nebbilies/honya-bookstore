package com.honya.bookstore.catalog.api;

import com.honya.bookstore.catalog.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CatalogStockApiAdapter implements CatalogStockApi {

    private final BookService bookService;

    @Override
    public void reduceStock(UUID bookId, Integer quantity) {
        bookService.reduceStock(bookId, quantity);
    }

    @Override
    public void addStock(UUID bookId, Integer quantity) {
        bookService.addStock(bookId, quantity);
    }

    @Override
    public Integer getBookPrice(UUID bookId) {
        return bookService.getBookPrice(bookId);
    }
}
