package com.honya.bookstore.order.api;

import java.util.List;

public record OrderRequest(
        String firstName,
        String lastName,
        String address,
        String city,
        List<OrderItemRequest> items,
        Integer totalAmount
) {
}
