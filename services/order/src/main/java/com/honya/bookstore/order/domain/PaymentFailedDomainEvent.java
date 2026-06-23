package com.honya.bookstore.order.domain;

import java.util.UUID;

public record PaymentFailedDomainEvent(UUID orderId, String reason) {
}
