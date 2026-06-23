package com.honya.bookstore.catalog.application;

import com.honya.bookstore.catalog.domain.Book;
import com.honya.bookstore.catalog.infrastructure.persistence.BookMediaRepository;
import com.honya.bookstore.catalog.infrastructure.persistence.BookRepository;
import com.honya.bookstore.catalog.infrastructure.persistence.CatalogStockReservationRepository;
import com.honya.bookstore.catalog.outbox.CatalogOutboxWriter;
import com.honya.bookstore.catalog.infrastructure.client.MediaClient;
import com.honya.bookstore.shared.error.InsufficientStockException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookServiceImplReservationTest {

    private BookServiceImpl service(BookRepository bookRepository, CatalogStockReservationRepository reservationRepository) {
        return new BookServiceImpl(
                bookRepository,
                mock(BookMediaRepository.class),
                mock(MediaClient.class),
                mock(CatalogOutboxWriter.class),
                reservationRepository);
    }

    @Test
    void reserveDeductsStockOnFirstReservation() {
        BookRepository bookRepository = mock(BookRepository.class);
        CatalogStockReservationRepository reservationRepository = mock(CatalogStockReservationRepository.class);
        UUID sagaId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        when(reservationRepository.insertIfAbsent(eq(sagaId), eq(bookId), eq(3), any(OffsetDateTime.class))).thenReturn(1);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(Book.builder().id(bookId).title("Demo").stockQuantity(10).build()));

        service(bookRepository, reservationRepository).reserveStock(sagaId, bookId, 3);

        ArgumentCaptor<Book> saved = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(saved.capture());
        assertEquals(7, saved.getValue().getStockQuantity());
    }

    @Test
    void reserveIsIdempotentAndDoesNotDeductWhenAlreadyReserved() {
        BookRepository bookRepository = mock(BookRepository.class);
        CatalogStockReservationRepository reservationRepository = mock(CatalogStockReservationRepository.class);
        UUID sagaId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        when(reservationRepository.insertIfAbsent(eq(sagaId), eq(bookId), eq(3), any(OffsetDateTime.class))).thenReturn(0);

        service(bookRepository, reservationRepository).reserveStock(sagaId, bookId, 3);

        verify(bookRepository, never()).findById(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void reserveThrowsInsufficientStockWhenNotEnough() {
        BookRepository bookRepository = mock(BookRepository.class);
        CatalogStockReservationRepository reservationRepository = mock(CatalogStockReservationRepository.class);
        UUID sagaId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        when(reservationRepository.insertIfAbsent(eq(sagaId), eq(bookId), eq(5), any(OffsetDateTime.class))).thenReturn(1);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(Book.builder().id(bookId).title("Demo").stockQuantity(2).build()));

        assertThrows(InsufficientStockException.class,
                () -> service(bookRepository, reservationRepository).reserveStock(sagaId, bookId, 5));
    }

    @Test
    void releaseRestoresStockWhenReservationWasHeld() {
        BookRepository bookRepository = mock(BookRepository.class);
        CatalogStockReservationRepository reservationRepository = mock(CatalogStockReservationRepository.class);
        UUID sagaId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        when(reservationRepository.markReleasedIfReserved(sagaId, bookId)).thenReturn(1);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(Book.builder().id(bookId).title("Demo").stockQuantity(7).build()));

        service(bookRepository, reservationRepository).releaseStock(sagaId, bookId, 3);

        ArgumentCaptor<Book> saved = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(saved.capture());
        assertEquals(10, saved.getValue().getStockQuantity());
    }

    @Test
    void releaseIsIdempotentAndDoesNotRestoreWhenNothingReserved() {
        BookRepository bookRepository = mock(BookRepository.class);
        CatalogStockReservationRepository reservationRepository = mock(CatalogStockReservationRepository.class);
        UUID sagaId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        when(reservationRepository.markReleasedIfReserved(sagaId, bookId)).thenReturn(0);

        service(bookRepository, reservationRepository).releaseStock(sagaId, bookId, 3);

        verify(bookRepository, never()).findById(any());
        verify(bookRepository, never()).save(any());
    }
}
