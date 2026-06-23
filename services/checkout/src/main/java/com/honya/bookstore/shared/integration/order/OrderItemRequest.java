package com.honya.bookstore.shared.integration.order;

import java.util.UUID;

public record OrderItemRequest(UUID bookId, Integer quantity, Integer price) {
}
