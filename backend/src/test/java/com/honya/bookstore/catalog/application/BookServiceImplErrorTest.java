package com.honya.bookstore.catalog.application;

import com.honya.bookstore.catalog.domain.Book;
import com.honya.bookstore.catalog.infrastructure.persistence.BookRepository;
import com.honya.bookstore.shared.error.InsufficientStockException;
import com.honya.bookstore.shared.error.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookServiceImplErrorTest {

    @Test
    void getBookByIdThrowsTypedNotFound() {
        BookRepository repository = mock(BookRepository.class);
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> new BookServiceImpl(repository).getBookById(id));
    }

    @Test
    void reduceStockThrowsTypedInsufficientStock() {
        BookRepository repository = mock(BookRepository.class);
        UUID id = UUID.randomUUID();
        Book book = Book.builder().id(id).title("Demo Book").stockQuantity(2).build();
        when(repository.findById(id)).thenReturn(Optional.of(book));

        assertThrows(InsufficientStockException.class, () -> new BookServiceImpl(repository).reduceStock(id, 5));
    }
}
