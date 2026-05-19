package com.honya.bookstore.catalog.api;

import java.util.UUID;

public record CatalogCartSnapshot(
        UUID id,
        String title,
        String author,
        String imageUrl,
        Integer price
) {
}
