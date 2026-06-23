package com.honya.bookstore.shared.integration.order;

public final class OrderEventsTopology {

    public static final String EXCHANGE = "order.events";
    public static final String ORDER_PLACED = "order.placed";
    public static final String PAYMENT_CONFIRMED = "order.payment.confirmed";
    public static final String PAYMENT_FAILED = "order.payment.failed";

    private OrderEventsTopology() {
    }
}
