package com.honya.bookstore.shared.integration.media.event;

import java.util.UUID;

public record MediaDeletedEvent(UUID eventId, UUID mediaId) {
}
