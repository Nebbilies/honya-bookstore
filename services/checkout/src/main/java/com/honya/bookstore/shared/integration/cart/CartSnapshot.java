package com.honya.bookstore.shared.integration.cart;

import java.util.List;
import java.util.UUID;

public record CartSnapshot(UUID userId, List<CartItemSnapshot> items) {
}
