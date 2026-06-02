package com.honya.bookstore.shared.integration.catalog.event;

import java.util.UUID;

public record ProductDetailsChangedEvent(
        UUID catalogItemId,
        String title,
        String author,
        String imageUrl,
        Integer price
) {
}
