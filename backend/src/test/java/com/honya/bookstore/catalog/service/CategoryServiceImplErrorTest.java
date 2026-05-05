package com.honya.bookstore.catalog.service;

import com.honya.bookstore.catalog.repo.CategoryRepository;
import com.honya.bookstore.catalog.service.impl.CategoryServiceImpl;
import com.honya.bookstore.shared.error.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CategoryServiceImplErrorTest {

    @Test
    void getCategoryByIdThrowsTypedNotFound() {
        CategoryRepository repository = mock(CategoryRepository.class);
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> new CategoryServiceImpl(repository).getCategoryById(id));
    }
}
