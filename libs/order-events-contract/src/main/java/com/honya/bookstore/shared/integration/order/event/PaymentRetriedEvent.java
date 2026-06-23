package com.honya.bookstore.shared.integration.order.event;

import java.util.UUID;

public record PaymentRetriedEvent(UUID orderId) {
}
