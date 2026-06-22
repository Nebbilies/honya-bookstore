package com.honya.bookstore.shared.integration.catalog;

import java.util.UUID;

public record CatalogBookView(
        UUID id,
        String title,
        String author,
        String imageUrl,
        Integer price
) {
}
