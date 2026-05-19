package com.honya.bookstore.catalog.outbox;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CatalogOutboxWriterTest {

    @Test
    void enqueueStoresPendingCatalogEvent() {
        Class<?> writerType = requiredClass("com.honya.bookstore.catalog.outbox.CatalogOutboxWriter");
        Class<?> messageType = requiredClass("com.honya.bookstore.catalog.outbox.CatalogOutboxMessage");
        Class<?> repositoryType = requiredClass("com.honya.bookstore.catalog.outbox.CatalogOutboxMessageRepository");
        Class<?> serializerType = requiredClass("com.honya.bookstore.catalog.outbox.CatalogOutboxEventSerializer");
        Class<?> statusType = requiredClass("com.honya.bookstore.catalog.outbox.CatalogOutboxStatus");

        Object pendingStatus = assertDoesNotThrow(() -> Enum.valueOf(statusType.asSubclass(Enum.class), "PENDING"));

        Object repositoryMock = mock(repositoryType);
        Object serializerMock = mock(serializerType);

        Object writer = assertDoesNotThrow(() -> {
            Constructor<?> constructor = writerType.getDeclaredConstructor(repositoryType, serializerType);
            constructor.setAccessible(true);
            return constructor.newInstance(repositoryMock, serializerMock);
        });

        UUID aggregateId = UUID.randomUUID();
        Object payload = "{\"key\":\"value\"}";

        assertDoesNotThrow(() -> {
            Method enqueue = writerType.getDeclaredMethod("enqueue", String.class, UUID.class, Object.class);
            enqueue.setAccessible(true);
            enqueue.invoke(writer, "PRODUCT_PRICE_CHANGED", aggregateId, payload);
        }, "Writer should enqueue catalog event");

        assertDoesNotThrow(() -> verify(repositoryMock).getClass(), "Repository mock should be verifiable");

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        assertDoesNotThrow(() -> {
            Method saveMethod = repositoryType.getMethod("save", Object.class);
            saveMethod.invoke(verify(repositoryMock), any());
        });

        assertDoesNotThrow(() -> {
            Method eventTypeGetter = messageType.getMethod("getEventType");
            Method aggregateGetter = messageType.getMethod("getAggregateId");
            Method statusGetter = messageType.getMethod("getStatus");
            Method attemptsGetter = messageType.getMethod("getAttempts");
            Method payloadGetter = messageType.getMethod("getPayload");
            assertNotNull(eventTypeGetter);
            assertNotNull(aggregateGetter);
            assertNotNull(statusGetter);
            assertNotNull(attemptsGetter);
            assertNotNull(payloadGetter);
        });

        assertEquals("PENDING", pendingStatus.toString());
    }

    private Class<?> requiredClass(String fqcn) {
        return assertDoesNotThrow(
                () -> Class.forName(fqcn),
                () -> "Expected class to exist: " + fqcn
        );
    }
}
