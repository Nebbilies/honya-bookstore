package com.honya.bookstore.catalog.service.impl;

import com.honya.bookstore.catalog.domain.Book;
import com.honya.bookstore.catalog.repo.BookRepository;
import com.honya.bookstore.catalog.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book getBookById(UUID id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + id));
    }

    @Override
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(UUID id, Book book) {
        Book existingBook = getBookById(id);

        existingBook.setTitle(book.getTitle());
        existingBook.setDescription(book.getDescription());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPrice(book.getPrice());
        existingBook.setPagesCount(book.getPagesCount());
        existingBook.setYearPublished(book.getYearPublished());
        existingBook.setPublisher(book.getPublisher());
        existingBook.setWeight(book.getWeight());
        existingBook.setStockQuantity(book.getStockQuantity());
        existingBook.setCategories(book.getCategories());

        return bookRepository.save(existingBook);
    }

    @Override
    public void deleteBook(UUID id) {
        bookRepository.deleteById(id);
    }

    @Override
    public Integer getBookPrice(UUID bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"))
                .getPrice();
    }

    @Override
    @Transactional
    public void reduceStock(UUID bookId, Integer quantity) {
        Book book = getBookById(bookId);

        if (book.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for book: " + book.getTitle());
        }

        book.setStockQuantity(book.getStockQuantity() - quantity);
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void addStock(UUID bookId, Integer quantity) {
        Book book = getBookById(bookId);

        book.setStockQuantity(book.getStockQuantity() + quantity);

        bookRepository.save(book);
    }
}