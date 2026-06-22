package com.honya.bookstore.shared.integration.catalog;

public final class CatalogEventsTopology {

    public static final String EXCHANGE = "catalog.events";
    public static final String PRICE_CHANGED = "product.price.changed";
    public static final String DETAILS_CHANGED = "product.details.changed";
    public static final String REMOVED = "product.removed";

    private CatalogEventsTopology() {
    }
}
