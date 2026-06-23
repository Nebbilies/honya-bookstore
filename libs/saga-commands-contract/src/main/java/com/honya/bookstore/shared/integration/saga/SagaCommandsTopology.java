package com.honya.bookstore.shared.integration.saga;

public final class SagaCommandsTopology {

    public static final String EXCHANGE = "saga.commands";
    public static final String STOCK_RELEASE = "saga.stock.release";
    public static final String ORDER_CANCEL = "saga.order.cancel";

    private SagaCommandsTopology() {
    }
}
