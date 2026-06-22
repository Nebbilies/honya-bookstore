package com.honya.bookstore.catalog.application;

import com.honya.bookstore.catalog.web.BookController.sortOrder;

import java.util.List;
import java.util.UUID;

public record BookSearchCriteria(
        Integer minPrice,
        Integer maxPrice,
        String publisher,
        List<UUID> categoryIds,
        Integer year,
        sortOrder sortPrice,
        sortOrder sortRating,
        String search
) {
}
