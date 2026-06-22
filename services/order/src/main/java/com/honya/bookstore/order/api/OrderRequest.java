package com.honya.bookstore.order.api;

import java.util.List;

public record OrderRequest(
        String firstName,
        String lastName,
        String address,
        String city,
        String email,
        String phone,
        String provider,
        String returnUrl,
        List<OrderItemRequest> items,
        Integer totalAmount
) {
}
