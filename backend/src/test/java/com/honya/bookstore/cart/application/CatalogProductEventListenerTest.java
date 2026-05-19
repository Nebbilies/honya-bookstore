package com.honya.bookstore.cart.application;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CatalogProductEventListenerTest {

    @Test
    void handleProductRemoved_removesMatchingCartItems() {
        Class<?> cartServiceType = requiredClass("com.honya.bookstore.cart.application.CartService");
        Class<?> listenerType = requiredClass("com.honya.bookstore.cart.application.CatalogProductEventListener");
        Class<?> removedEventType = requiredClass("com.honya.bookstore.catalog.api.event.ProductRemovedEvent");

        UUID catalogItemId = UUID.randomUUID();
        Object cartServiceMock = mock(cartServiceType);

        Object listener = assertDoesNotThrow(() -> listenerType.getDeclaredConstructor(cartServiceType).newInstance(cartServiceMock));
        Object removedEvent = assertDoesNotThrow(() -> instantiateRemovedEvent(removedEventType, catalogItemId));

        assertDoesNotThrow(() -> {
            Method handle = listenerType.getDeclaredMethod("handleProductRemoved", removedEventType);
            handle.setAccessible(true);
            handle.invoke(listener, removedEvent);
        }, "Listener should consume ProductRemovedEvent");

        assertDoesNotThrow(() -> {
            Object verified = verify(cartServiceMock);
            Method removeMethod = cartServiceType.getMethod("removeItemsByCatalogItemId", UUID.class);
            removeMethod.invoke(verified, catalogItemId);
        }, "Listener should remove matching cart items by catalog item id");
    }

    private Object instantiateRemovedEvent(Class<?> eventType, UUID catalogItemId) throws Exception {
        for (Constructor<?> constructor : eventType.getDeclaredConstructors()) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length == 1 && parameterTypes[0].equals(UUID.class)) {
                constructor.setAccessible(true);
                return constructor.newInstance(catalogItemId);
            }
        }
        throw new NoSuchMethodException("Expected ProductRemovedEvent(UUID) constructor");
    }

    private Class<?> requiredClass(String fqcn) {
        return assertDoesNotThrow(
                () -> Class.forName(fqcn),
                () -> "Expected class to exist: " + fqcn
        );
    }
}
