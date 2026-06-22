package com.honya.bookstore.shared.integration.catalog.event;

import java.util.UUID;

public record ProductRemovedEvent(UUID catalogItemId) {
}
