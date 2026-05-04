package com.honya.bookstore.catalog.api;

import java.util.UUID;

public interface CatalogStockApi {
    void reduceStock(UUID bookId, Integer quantity);
    void addStock(UUID bookId, Integer quantity);
    Integer getBookPrice(UUID bookId);
}
