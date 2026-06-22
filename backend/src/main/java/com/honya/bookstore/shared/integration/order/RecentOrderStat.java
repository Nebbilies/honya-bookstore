package com.honya.bookstore.shared.integration.order;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RecentOrderStat(UUID id, OffsetDateTime createdAt, int totalAmount) {
}
