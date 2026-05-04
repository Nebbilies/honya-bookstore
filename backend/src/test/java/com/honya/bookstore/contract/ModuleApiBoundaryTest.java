package com.honya.bookstore.contract;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModuleApiBoundaryTest {

    @Test
    void catalogApiContractMustExist() throws Exception {
        Class<?> type = Class.forName("com.honya.bookstore.catalog.api.CatalogStockApi");
        assertEquals("com.honya.bookstore.catalog.api", type.getPackageName());
    }

    @Test
    void cartApiContractMustExist() throws Exception {
        Class<?> type = Class.forName("com.honya.bookstore.cart.api.CartApi");
        assertEquals("com.honya.bookstore.cart.api", type.getPackageName());
    }

    @Test
    void orderApiContractMustExist() throws Exception {
        Class<?> type = Class.forName("com.honya.bookstore.order.api.OrderApi");
        assertEquals("com.honya.bookstore.order.api", type.getPackageName());
    }

    @Test
    void serviceInterfacesMustNotExtendApiContracts() {
        assertTrue(Arrays.stream(com.honya.bookstore.order.OrderService.class.getInterfaces())
                .noneMatch(type -> type.getName().equals("com.honya.bookstore.order.api.OrderApi")));
        assertTrue(Arrays.stream(com.honya.bookstore.cart.CartService.class.getInterfaces())
                .noneMatch(type -> type.getName().equals("com.honya.bookstore.cart.api.CartApi")));
        assertTrue(Arrays.stream(com.honya.bookstore.catalog.service.BookService.class.getInterfaces())
                .noneMatch(type -> type.getName().equals("com.honya.bookstore.catalog.api.CatalogStockApi")));
    }

    @Test
    void apiAdaptersMustExistInEachModule() throws Exception {
        Class<?> catalogAdapter = Class.forName("com.honya.bookstore.catalog.api.CatalogStockApiAdapter");
        Class<?> cartAdapter = Class.forName("com.honya.bookstore.cart.api.CartApiAdapter");
        Class<?> orderAdapter = Class.forName("com.honya.bookstore.order.api.OrderApiAdapter");

        assertEquals("com.honya.bookstore.catalog.api", catalogAdapter.getPackageName());
        assertEquals("com.honya.bookstore.cart.api", cartAdapter.getPackageName());
        assertEquals("com.honya.bookstore.order.api", orderAdapter.getPackageName());
    }

    @Test
    void cartApiSnapshotTypesMustExist() throws Exception {
        Class<?> cartSnapshot = Class.forName("com.honya.bookstore.cart.api.CartSnapshot");
        Class<?> cartItemSnapshot = Class.forName("com.honya.bookstore.cart.api.CartItemSnapshot");

        assertEquals("com.honya.bookstore.cart.api", cartSnapshot.getPackageName());
        assertEquals("com.honya.bookstore.cart.api", cartItemSnapshot.getPackageName());
    }
}
