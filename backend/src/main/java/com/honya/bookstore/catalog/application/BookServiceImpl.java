package com.honya.bookstore.catalog.application;

import com.honya.bookstore.catalog.domain.Book;
import com.honya.bookstore.catalog.infrastructure.persistence.BookRepository;
import com.honya.bookstore.catalog.infrastructure.persistence.BookSpecifications;
import com.honya.bookstore.catalog.web.BookController.sortOrder;
import com.honya.bookstore.shared.error.InsufficientStockException;
import com.honya.bookstore.shared.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public Page<Book> getAllBooks(BookSearchCriteria criteria, Pageable pageable) {
        Specification<Book> specification = Specification.where(BookSpecifications.minPrice(criteria.minPrice()))
                .and(BookSpecifications.maxPrice(criteria.maxPrice()))
                .and(BookSpecifications.publisher(criteria.publisher()))
                .and(BookSpecifications.year(criteria.year()))
                .and(BookSpecifications.categoryIdsAny(criteria.categoryIds()));

        Sort sort = buildSort(criteria);
        Pageable sortedPageable = sort.isSorted()
                ? PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort)
                : pageable;

        return bookRepository.findAll(specification, sortedPageable);
    }

    @Override
    public Book getBookById(UUID id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", id));
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
                .orElseThrow(() -> new ResourceNotFoundException("Book", bookId))
                .getPrice();
    }

    @Override
    @Transactional
    public void reduceStock(UUID bookId, Integer quantity) {
        Book book = getBookById(bookId);

        if (book.getStockQuantity() < quantity) {
            throw new InsufficientStockException(book.getId(), book.getTitle(), quantity, book.getStockQuantity());
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

    private Sort buildSort(BookSearchCriteria criteria) {
        List<Sort.Order> orders = new ArrayList<>();

        if (criteria.sortPrice() != null) {
            orders.add(new Sort.Order(toDirection(criteria.sortPrice()), "price"));
        }

        if (criteria.sortRating() != null) {
            orders.add(new Sort.Order(toDirection(criteria.sortRating()), "rating"));
        }

        return orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
    }

    private Sort.Direction toDirection(sortOrder order) {
        return order == sortOrder.asc ? Sort.Direction.ASC : Sort.Direction.DESC;
    }
}