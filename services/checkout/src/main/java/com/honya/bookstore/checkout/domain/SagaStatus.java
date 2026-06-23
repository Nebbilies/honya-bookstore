package com.honya.bookstore.checkout.domain;

public enum SagaStatus {
    STARTED,
    STOCK_RESERVED,
    ORDER_CREATED,
    AWAITING_PAYMENT,
    COMPLETED,
    COMPENSATED,
    REFUND_REQUIRED
}
