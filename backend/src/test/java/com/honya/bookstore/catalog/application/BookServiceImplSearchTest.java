package com.honya.bookstore.catalog.application;

import com.honya.bookstore.catalog.domain.Book;
import com.honya.bookstore.catalog.infrastructure.persistence.BookRepository;
import com.honya.bookstore.catalog.web.BookController.sortOrder;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookServiceImplSearchTest {

    @Test
    void getAllBooksBuildsSortWithPriceThenRating() {
        BookRepository repository = mock(BookRepository.class);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());

        BookServiceImpl service = new BookServiceImpl(repository);
        BookSearchCriteria criteria = new BookSearchCriteria(
                100,
                300,
                "Pub",
                List.of(UUID.randomUUID()),
                2020,
                sortOrder.asc,
                sortOrder.desc
        );

        service.getAllBooks(criteria, PageRequest.of(0, 10));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(any(Specification.class), pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertEquals("price: ASC,rating: DESC", pageable.getSort().toString());
    }

    @Test
    void getAllBooksComposesSpecificationAndCallsFindAll() {
        BookRepository repository = mock(BookRepository.class);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());

        BookServiceImpl service = new BookServiceImpl(repository);
        BookSearchCriteria criteria = new BookSearchCriteria(
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        service.getAllBooks(criteria, PageRequest.of(0, 10));

        ArgumentCaptor<Specification<Book>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(repository, times(1)).findAll(specCaptor.capture(), any(Pageable.class));
        assertNotNull(specCaptor.getValue());
    }
}
