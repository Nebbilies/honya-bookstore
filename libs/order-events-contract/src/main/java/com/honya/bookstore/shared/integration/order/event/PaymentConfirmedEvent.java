package com.honya.bookstore.shared.integration.order.event;

import java.util.UUID;

public record PaymentConfirmedEvent(UUID orderId, String transactionNo) {
}
