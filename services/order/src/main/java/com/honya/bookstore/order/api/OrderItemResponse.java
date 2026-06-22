package com.honya.bookstore.order.api;

import java.util.UUID;

public record OrderItemResponse(UUID id, UUID bookId, Integer quantity, Integer price) {
}
