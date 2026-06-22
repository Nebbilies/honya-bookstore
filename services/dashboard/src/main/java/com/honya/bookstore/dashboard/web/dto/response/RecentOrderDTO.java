package com.honya.bookstore.dashboard.web.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RecentOrderDTO(UUID id, OffsetDateTime createdAt, int totalAmount) {
}
