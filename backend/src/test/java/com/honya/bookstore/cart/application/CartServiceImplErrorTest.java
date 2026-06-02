package com.honya.bookstore.cart.application;

import com.honya.bookstore.cart.infrastructure.persistence.CartRepository;
import com.honya.bookstore.catalog.api.CatalogStockApi;
import com.honya.bookstore.shared.error.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CartServiceImplErrorTest {

    @Test
    void clearCartThrowsTypedNotFound() {
        CartRepository repository = mock(CartRepository.class);
        CatalogStockApi catalogStockApi = mock(CatalogStockApi.class);
        UUID userId = UUID.randomUUID();
        when(repository.findByOwnerId(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> new CartServiceImpl(repository, catalogStockApi).clearCart(userId));
    }
}
