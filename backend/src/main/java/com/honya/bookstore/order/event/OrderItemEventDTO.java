package com.honya.bookstore.order.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class OrderItemEventDTO {
    private final UUID bookId;
    private final Integer quantity;
}