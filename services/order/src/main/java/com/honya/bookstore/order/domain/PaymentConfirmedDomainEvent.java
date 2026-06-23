package com.honya.bookstore.order.domain;

import java.util.UUID;

public record PaymentConfirmedDomainEvent(UUID orderId, String transactionNo) {
}
