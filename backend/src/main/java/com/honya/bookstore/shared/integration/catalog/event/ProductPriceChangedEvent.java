package com.honya.bookstore.shared.integration.catalog.event;

import java.util.UUID;

public record ProductPriceChangedEvent(UUID catalogItemId, Integer price) {
}
