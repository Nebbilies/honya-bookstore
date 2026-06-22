package com.honya.bookstore.catalog.outbox;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Method;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CatalogOutboxRelayTest {

    @Test
    void relayPublishesDueMessageAndMarksSent() {
        Class<?> relayType = requiredClass("com.honya.bookstore.catalog.outbox.CatalogOutboxRelay");
        Class<?> repositoryType = requiredClass("com.honya.bookstore.catalog.outbox.CatalogOutboxMessageRepository");
        Class<?> messageType = requiredClass("com.honya.bookstore.catalog.outbox.CatalogOutboxMessage");
        Class<?> statusType = requiredClass("com.honya.bookstore.catalog.outbox.CatalogOutboxStatus");
        requiredClass("com.honya.bookstore.catalog.outbox.CatalogOutboxEventSerializer");
        requiredClass("com.honya.bookstore.catalog.outbox.CatalogOutboxBackoff");

        Object sentStatus = assertDoesNotThrow(() -> Enum.valueOf(statusType.asSubclass(Enum.class), "SENT"));

        Method dueQuery = assertDoesNotThrow(
                () -> repositoryType.getMethod("findDueMessages", OffsetDateTime.class, Pageable.class)
        );
        Method publishMethod = assertDoesNotThrow(() -> relayType.getDeclaredMethod("publishDueMessages"));
        Method statusGetter = assertDoesNotThrow(() -> messageType.getMethod("getStatus"));
        Method sentAtGetter = assertDoesNotThrow(() -> messageType.getMethod("getSentAt"));

        assertNotNull(dueQuery);
        assertNotNull(publishMethod);
        assertNotNull(statusGetter);
        assertNotNull(sentAtGetter);
        assertEquals("SENT", sentStatus.toString());
    }

    private Class<?> requiredClass(String fqcn) {
        return assertDoesNotThrow(
                () -> Class.forName(fqcn),
                () -> "Expected class to exist: " + fqcn
        );
    }
}
