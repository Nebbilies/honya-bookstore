package com.honya.bookstore.shared.integration.order;

import java.util.UUID;

public record OrderItemResponse(UUID id, UUID bookId, Integer quantity, Integer price) {
}
