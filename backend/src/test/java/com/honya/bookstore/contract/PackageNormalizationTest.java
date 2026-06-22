package com.honya.bookstore.contract;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PackageNormalizationTest {

    @Test
    void moduleTypesMustLiveInStandardPackages() throws Exception {
        assertEquals("com.honya.bookstore.discount.domain", Class.forName("com.honya.bookstore.discount.domain.Discount").getPackageName());
        assertEquals("com.honya.bookstore.ticket.domain", Class.forName("com.honya.bookstore.ticket.domain.Ticket").getPackageName());
    }

    @Test
    void legacyFeatureRootEntitiesMustNotExist() {
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.honya.bookstore.domain.entity.User"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.honya.bookstore.discount.Discount"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.honya.bookstore.ticket.Ticket"));
    }
}
