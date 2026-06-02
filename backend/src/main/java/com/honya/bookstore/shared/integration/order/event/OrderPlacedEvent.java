package com.honya.bookstore.shared.integration.order.event;

import java.util.List;
import java.util.UUID;

public record OrderPlacedEvent(UUID orderId, UUID userId, List<OrderItemEventDTO> items) {
}
