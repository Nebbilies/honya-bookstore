package com.honya.bookstore.order.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class OrderPlacedEvent {
    private final UUID orderId;
    private final UUID userId;
    private final List<OrderItemEventDTO> items;
}
