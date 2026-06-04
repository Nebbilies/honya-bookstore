package com.honya.bookstore.catalog.application;

import com.honya.bookstore.catalog.domain.Book;
import com.honya.bookstore.catalog.infrastructure.persistence.BookMediaRepository;
import com.honya.bookstore.catalog.infrastructure.persistence.BookRepository;
import com.honya.bookstore.catalog.outbox.CatalogOutboxWriter;
import com.honya.bookstore.media.api.MediaApi;
import com.honya.bookstore.shared.error.InsufficientStockException;
import com.honya.bookstore.shared.error.ResourceNotFoundException;
import com.honya.bookstore.shared.integration.catalog.event.ProductRemovedEvent;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookServiceImplErrorTest {

    @Test
    void getBookByIdThrowsTypedNotFound() {
        BookRepository repository = mock(BookRepository.class);
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> new BookServiceImpl(repository, mock(BookMediaRepository.class), mock(MediaApi.class), mock(CatalogOutboxWriter.class)).getBookById(id));
    }

    @Test
    void reduceStockThrowsTypedInsufficientStock() {
        BookRepository repository = mock(BookRepository.class);
        UUID id = UUID.randomUUID();
        Book book = Book.builder().id(id).title("Demo Book").stockQuantity(2).build();
        when(repository.findById(id)).thenReturn(Optional.of(book));

        assertThrows(InsufficientStockException.class, () -> new BookServiceImpl(repository, mock(BookMediaRepository.class), mock(MediaApi.class), mock(CatalogOutboxWriter.class)).reduceStock(id, 5));
    }

    @Test
    void deleteBookEnqueuesProductRemovedEvent() {
        BookRepository repository = mock(BookRepository.class);
        BookMediaRepository mediaRepository = mock(BookMediaRepository.class);
        CatalogOutboxWriter outboxWriter = mock(CatalogOutboxWriter.class);
        UUID id = UUID.randomUUID();
        Book existingBook = Book.builder().id(id).title("Demo Book").price(120).build();
        when(repository.findById(id)).thenReturn(Optional.of(existingBook));

        new BookServiceImpl(repository, mediaRepository, mock(MediaApi.class), outboxWriter).deleteBook(id);

        verify(outboxWriter).enqueue(eq("PRODUCT_REMOVED"), eq(id), any(ProductRemovedEvent.class));
        verify(mediaRepository).deleteByBookId(id);
        verify(repository).deleteById(id);
    }
}
