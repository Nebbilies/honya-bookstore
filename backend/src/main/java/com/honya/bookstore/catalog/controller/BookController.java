package com.honya.bookstore.catalog.controller;

import com.honya.bookstore.catalog.domain.Book;
import com.honya.bookstore.catalog.domain.Category;
import com.honya.bookstore.catalog.dto.request.BookRequestDTO;
import com.honya.bookstore.catalog.dto.response.BookResponseDTO;
import com.honya.bookstore.catalog.dto.response.CategoryResponseDTO;

import com.honya.bookstore.catalog.service.BookService;
import com.honya.bookstore.catalog.service.CategoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        List<BookResponseDTO> books = bookService.getAllBooks().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable UUID id) {
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok(mapToResponseDTO(book));
    }

    @PostMapping
    public ResponseEntity<BookResponseDTO> createBook(@RequestBody BookRequestDTO requestDTO) {
        List<Category> categories = requestDTO.getCategoryIds().stream()
                .map(categoryService::getCategoryById)
                .collect(Collectors.toList());

        Book book = Book.builder()
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .author(requestDTO.getAuthor())
                .price(requestDTO.getPrice())
                .pagesCount(requestDTO.getPagesCount())
                .yearPublished(requestDTO.getYearPublished())
                .publisher(requestDTO.getPublisher())
                .weight(requestDTO.getWeight())
                .stockQuantity(requestDTO.getStockQuantity())
                .categories(categories)
                .build();

        Book savedBook = bookService.createBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDTO(savedBook));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable UUID id, @RequestBody BookRequestDTO requestDTO) {
        List<Category> categories = requestDTO.getCategoryIds().stream()
                .map(categoryService::getCategoryById)
                .collect(Collectors.toList());

        Book bookDetails = Book.builder()
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .author(requestDTO.getAuthor())
                .price(requestDTO.getPrice())
                .pagesCount(requestDTO.getPagesCount())
                .yearPublished(requestDTO.getYearPublished())
                .publisher(requestDTO.getPublisher())
                .weight(requestDTO.getWeight())
                .stockQuantity(requestDTO.getStockQuantity())
                .categories(categories)
                .build();

        Book updatedBook = bookService.updateBook(id, bookDetails);
        return ResponseEntity.ok(mapToResponseDTO(updatedBook));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // Helper method to convert Entity to DTO
    private BookResponseDTO mapToResponseDTO(Book book) {
        List<CategoryResponseDTO> categoryDTOs = book.getCategories() != null ?
                book.getCategories().stream()
                        .map(c -> CategoryResponseDTO.builder()
                                .id(c.getId())
                                .name(c.getName())
                                .slug(c.getSlug())
                                .description(c.getDescription())
                                .build())
                        .collect(Collectors.toList())
                : null;

        return BookResponseDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .description(book.getDescription())
                .author(book.getAuthor())
                .price(book.getPrice())
                .pagesCount(book.getPagesCount())
                .yearPublished(book.getYearPublished())
                .publisher(book.getPublisher())
                .weight(book.getWeight())
                .stockQuantity(book.getStockQuantity())
                .purchaseCount(book.getPurchaseCount())
                .rating(book.getRating())
                .categories(categoryDTOs)
                .build();
    }
}