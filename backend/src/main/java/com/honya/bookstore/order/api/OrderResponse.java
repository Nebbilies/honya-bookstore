package com.honya.bookstore.order.api;

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
        java.time.OffsetDateTime createdAt,
        java.time.OffsetDateTime updatedAt,
        List<OrderItemResponse> items
) {
}
