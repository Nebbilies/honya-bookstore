package com.honya.bookstore.order.api;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RecentOrderStat(UUID id, OffsetDateTime createdAt, int totalAmount) {
}
