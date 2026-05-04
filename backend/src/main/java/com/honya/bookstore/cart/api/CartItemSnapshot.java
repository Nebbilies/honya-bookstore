package com.honya.bookstore.cart.api;

import java.util.UUID;

public record CartItemSnapshot(UUID bookId, Integer quantity) {
}
