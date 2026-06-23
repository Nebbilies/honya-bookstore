package com.honya.bookstore.shared.integration.order;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String firstName,
        String lastName,
        String address,
        String city,
        String email,
        String phone,
        String paymentUrl,
        String provider,
        String status,
        Boolean isPaid,
        Integer totalAmount,
        UUID userId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<OrderItemResponse> items
) {
}
