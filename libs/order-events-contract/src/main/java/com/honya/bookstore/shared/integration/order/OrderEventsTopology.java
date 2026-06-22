package com.honya.bookstore.shared.integration.order;

public final class OrderEventsTopology {

    public static final String EXCHANGE = "order.events";
    public static final String ORDER_PLACED = "order.placed";

    private OrderEventsTopology() {
    }
}
