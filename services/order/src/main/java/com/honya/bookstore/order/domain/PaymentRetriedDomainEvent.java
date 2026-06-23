package com.honya.bookstore.order.domain;

import java.util.UUID;

public record PaymentRetriedDomainEvent(UUID orderId) {
}
