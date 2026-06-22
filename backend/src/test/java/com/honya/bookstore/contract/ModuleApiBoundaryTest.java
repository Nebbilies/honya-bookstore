package com.honya.bookstore.contract;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModuleApiBoundaryTest {

    @Test
    void orderApiContractMustExist() throws Exception {
        Class<?> type = Class.forName("com.honya.bookstore.order.api.OrderApi");
        assertEquals("com.honya.bookstore.order.api", type.getPackageName());
    }

    @Test
    void serviceInterfacesMustNotExtendApiContracts() {
        assertTrue(Arrays.stream(com.honya.bookstore.order.application.OrderService.class.getInterfaces())
                .noneMatch(type -> type.getName().equals("com.honya.bookstore.order.api.OrderApi")));
    }

    @Test
    void apiAdaptersMustExistInEachModule() throws Exception {
        Class<?> orderAdapter = Class.forName("com.honya.bookstore.order.api.OrderApiAdapter");

        assertEquals("com.honya.bookstore.order.api", orderAdapter.getPackageName());
    }
}
