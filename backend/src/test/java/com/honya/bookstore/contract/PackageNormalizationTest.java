package com.honya.bookstore.contract;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PackageNormalizationTest {

    @Test
    void moduleTypesMustLiveInStandardPackages() throws Exception {
        assertEquals("com.honya.bookstore.catalog.web", Class.forName("com.honya.bookstore.catalog.web.BookController").getPackageName());
        assertEquals("com.honya.bookstore.catalog.application", Class.forName("com.honya.bookstore.catalog.application.BookService").getPackageName());
        assertEquals("com.honya.bookstore.catalog.infrastructure.persistence", Class.forName("com.honya.bookstore.catalog.infrastructure.persistence.BookRepository").getPackageName());
        assertEquals("com.honya.bookstore.cart.web", Class.forName("com.honya.bookstore.cart.web.CartController").getPackageName());
        assertEquals("com.honya.bookstore.cart.application", Class.forName("com.honya.bookstore.cart.application.CartService").getPackageName());
        assertEquals("com.honya.bookstore.cart.infrastructure.persistence", Class.forName("com.honya.bookstore.cart.infrastructure.persistence.CartRepository").getPackageName());
        assertEquals("com.honya.bookstore.order.web", Class.forName("com.honya.bookstore.order.web.OrderController").getPackageName());
        assertEquals("com.honya.bookstore.order.application", Class.forName("com.honya.bookstore.order.application.OrderService").getPackageName());
        assertEquals("com.honya.bookstore.order.infrastructure.persistence", Class.forName("com.honya.bookstore.order.infrastructure.persistence.OrderRepository").getPackageName());
        assertEquals("com.honya.bookstore.user.domain", Class.forName("com.honya.bookstore.user.domain.User").getPackageName());
        assertEquals("com.honya.bookstore.article.domain", Class.forName("com.honya.bookstore.article.domain.Article").getPackageName());
        assertEquals("com.honya.bookstore.review.domain", Class.forName("com.honya.bookstore.review.domain.Review").getPackageName());
        assertEquals("com.honya.bookstore.discount.domain", Class.forName("com.honya.bookstore.discount.domain.Discount").getPackageName());
        assertEquals("com.honya.bookstore.ticket.domain", Class.forName("com.honya.bookstore.ticket.domain.Ticket").getPackageName());
    }

    @Test
    void legacyFeatureRootEntitiesMustNotExist() {
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.honya.bookstore.domain.entity.User"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.honya.bookstore.catalog.controller.BookController"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.honya.bookstore.catalog.service.BookService"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.honya.bookstore.catalog.repo.BookRepository"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.honya.bookstore.cart.CartController"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.honya.bookstore.cart.CartService"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.honya.bookstore.cart.CartRepository"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.honya.bookstore.order.OrderController"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.honya.bookstore.order.OrderService"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.honya.bookstore.order.OrderRepository"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.honya.bookstore.user.User"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.honya.bookstore.article.Article"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.honya.bookstore.review.Review"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.honya.bookstore.discount.Discount"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.honya.bookstore.ticket.Ticket"));
    }
}
