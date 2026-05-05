package com.honya.bookstore.catalog.application;

import com.honya.bookstore.catalog.domain.Book;
import java.util.List;
import java.util.UUID;

public interface BookService {
    List<Book> getAllBooks();
    Book getBookById(UUID id);
    Book createBook(Book book);
    Book updateBook(UUID id, Book book);
    void deleteBook(UUID id);
    Integer getBookPrice(UUID bookId);
    void reduceStock(UUID bookId, Integer quantity);
    void addStock(UUID bookId, Integer quantity);
}