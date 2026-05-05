package com.honya.bookstore.order.api;

import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String firstName,
        String lastName,
        String address,
        String city,
        String provider,
        String status,
        Boolean isPaid,
        Integer totalAmount,
        UUID userId,
        List<OrderItemResponse> items
) {
}
