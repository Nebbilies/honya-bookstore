package com.honya.bookstore.catalog.web;

import com.honya.bookstore.catalog.domain.Book;
import com.honya.bookstore.catalog.domain.Category;
import com.honya.bookstore.catalog.web.dto.request.BookRequestDTO;
import com.honya.bookstore.catalog.web.dto.response.BookMediaResponseDTO;
import com.honya.bookstore.catalog.web.dto.response.BookResponseDTO;
import com.honya.bookstore.catalog.web.dto.response.CategoryResponseDTO;

import com.honya.bookstore.catalog.application.BookService;
import com.honya.bookstore.catalog.application.CategoryService;
import com.honya.bookstore.shared.PageMetaDTO;
import com.honya.bookstore.shared.PagedResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Books", description = "Endpoints for managing books in the catalog")
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final CategoryService categoryService;

    @Operation(summary = "Get all books", description = "Retrieve all books in catalog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books retrieved")
    })
    @GetMapping
    public ResponseEntity<PagedResponseDTO<BookResponseDTO>> getAllBooks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        List<BookResponseDTO> books = bookService.getAllBooks().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        int safePage = Math.max(page, 1);
        int safeLimit = Math.max(limit, 1);
        int totalItems = books.size();
        int fromIndex = Math.min((safePage - 1) * safeLimit, totalItems);
        int toIndex = Math.min(fromIndex + safeLimit, totalItems);
        List<BookResponseDTO> pageData = books.subList(fromIndex, toIndex);
        int totalPages = totalItems == 0 ? 0 : (int) Math.ceil((double) totalItems / safeLimit);

        PageMetaDTO meta = new PageMetaDTO(
                safePage,
                safeLimit,
                pageData.size(),
                totalItems,
                totalPages
        );

        return ResponseEntity.ok(new PagedResponseDTO<>(pageData, meta));
    }

    @Operation(summary = "Get book by id", description = "Retrieve one book by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book retrieved"),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable UUID id) {
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok(mapToResponseDTO(book));
    }

    @Operation(summary = "Create book", description = "Create new book in catalog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book created"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
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
        BookResponseDTO responseDTO = mapToResponseDTO(savedBook);
        responseDTO.setMedia(mapRequestMedia(requestDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @Operation(summary = "Update book", description = "Update existing book by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Book or category not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
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
        BookResponseDTO responseDTO = mapToResponseDTO(updatedBook);
        responseDTO.setMedia(mapRequestMedia(requestDTO));
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Delete book", description = "Delete book by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book deleted"),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // Helper method to convert Entity to DTO
    private List<BookMediaResponseDTO> mapRequestMedia(BookRequestDTO requestDTO) {
        if (requestDTO.getMedia() == null) {
            return List.of();
        }

        return requestDTO.getMedia().stream()
                .map(media -> BookMediaResponseDTO.builder()
                        .id(media.getMediaId())
                        .isCover(media.getIsCover())
                        .build())
                .toList();
    }

    private BookResponseDTO mapToResponseDTO(Book book) {
        List<CategoryResponseDTO> categoryDTOs = book.getCategories() != null ?
                book.getCategories().stream()
                        .map(c -> CategoryResponseDTO.builder()
                                .id(c.getId())
                                .name(c.getName())
                                .slug(c.getSlug())
                                .description(c.getDescription())
                                .createdAt(c.getCreatedAt())
                                .updatedAt(c.getUpdatedAt())
                                .deletedAt(c.getDeletedAt())
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
                .media(List.<BookMediaResponseDTO>of())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .deletedAt(book.getDeletedAt())
                .build();
    }
}