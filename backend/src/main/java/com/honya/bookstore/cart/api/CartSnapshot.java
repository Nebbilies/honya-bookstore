package com.honya.bookstore.cart.api;

import java.util.List;
import java.util.UUID;

public record CartSnapshot(UUID userId, List<CartItemSnapshot> items) {
}
