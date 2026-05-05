package com.honya.bookstore.cart;

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
        UUID userId = UUID.randomUUID();
        when(repository.findByOwnerId(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> new CartServiceImpl(repository).clearCart(userId));
    }
}
