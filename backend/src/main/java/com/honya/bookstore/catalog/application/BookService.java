package com.honya.bookstore.catalog.application;

import com.honya.bookstore.catalog.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface BookService {
    Page<Book> getAllBooks(BookSearchCriteria criteria, Pageable pageable);
    Book getBookById(UUID id);
    Book createBook(Book book);
    Book updateBook(UUID id, Book book);
    void deleteBook(UUID id);
    Integer getBookPrice(UUID bookId);
    void reduceStock(UUID bookId, Integer quantity);
    void addStock(UUID bookId, Integer quantity);
}