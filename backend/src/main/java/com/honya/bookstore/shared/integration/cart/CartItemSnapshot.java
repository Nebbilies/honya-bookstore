package com.honya.bookstore.shared.integration.cart;

import java.util.UUID;

public record CartItemSnapshot(UUID bookId, Integer quantity) {
}
