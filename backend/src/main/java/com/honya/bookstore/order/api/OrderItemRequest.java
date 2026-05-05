package com.honya.bookstore.order.api;

import java.util.UUID;

public record OrderItemRequest(UUID bookId, Integer quantity, Integer price) {
}
