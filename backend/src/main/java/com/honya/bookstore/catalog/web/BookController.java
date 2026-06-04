package com.honya.bookstore.catalog.web;

import com.honya.bookstore.catalog.domain.Book;
import com.honya.bookstore.catalog.domain.Category;
import com.honya.bookstore.catalog.web.dto.request.BookRequestDTO;
import com.honya.bookstore.catalog.web.dto.response.BookMediaResponseDTO;
import com.honya.bookstore.catalog.web.dto.response.BookResponseDTO;
import com.honya.bookstore.catalog.web.dto.response.CategoryResponseDTO;

import com.honya.bookstore.catalog.application.BookSearchCriteria;
import com.honya.bookstore.catalog.application.BookService;
import com.honya.bookstore.catalog.application.CategoryService;
import com.honya.bookstore.security.StaffOrAdmin;
import com.honya.bookstore.shared.PageMetaDTO;
import com.honya.bookstore.shared.PagedResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Books", description = "Endpoints for managing books in the catalog")
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    public enum sortOrder {
        asc, desc
    }

    private final BookService bookService;
    private final CategoryService categoryService;

    @Operation(summary = "Get all books", description = "Retrieve all books in catalog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books retrieved")
    })
    @GetMapping
    public ResponseEntity<PagedResponseDTO<BookResponseDTO>> getAllBooks(
            @RequestParam() Optional<Integer> min_price,
            @RequestParam() Optional<Integer> max_price,
            @RequestParam() Optional<String> publisher,
            @RequestParam() Optional<ArrayList<UUID>> category_ids,
            @RequestParam() Optional<sortOrder> sort_price,
            @RequestParam() Optional<sortOrder> sort_rating,
            @RequestParam() Optional<Integer> year,
            @RequestParam() Optional<String> search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {

        if (min_price.isPresent() && max_price.isPresent() && min_price.get() > max_price.get()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "min_price must be <= max_price");
        }

        int safePage = Math.max(page, 1);
        int safeLimit = Math.max(limit, 1);

        BookSearchCriteria criteria = new BookSearchCriteria(
                min_price.orElse(null),
                max_price.orElse(null),
                publisher.orElse(null),
                category_ids.filter(ids -> !ids.isEmpty()).map(List::copyOf).orElse(null),
                year.orElse(null),
                sort_price.orElse(null),
                sort_rating.orElse(null),
                search.orElse(null)
        );

        Pageable pageable = PageRequest.of(safePage - 1, safeLimit);
        Page<Book> bookPage = bookService.getAllBooks(criteria, pageable);

        List<BookResponseDTO> pageData = bookPage.getContent().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        PageMetaDTO meta = new PageMetaDTO(
                safePage,
                safeLimit,
                pageData.size(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages()
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
    @StaffOrAdmin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book created"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    public ResponseEntity<BookResponseDTO> createBook(@RequestBody BookRequestDTO requestDTO) {
        validateMediaRequest(requestDTO);

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

        Book savedBook = bookService.createBook(book, requestDTO.getMedia());
        BookResponseDTO responseDTO = mapToResponseDTO(savedBook);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @Operation(summary = "Update book", description = "Update existing book by id")
    @StaffOrAdmin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Book or category not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable UUID id, @RequestBody BookRequestDTO requestDTO) {
        validateMediaRequest(requestDTO);

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

        Book updatedBook = bookService.updateBook(id, bookDetails, requestDTO.getMedia());
        BookResponseDTO responseDTO = mapToResponseDTO(updatedBook);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Delete book", description = "Delete book by id")
    @StaffOrAdmin
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

    private void validateMediaRequest(BookRequestDTO requestDTO) {
        if (requestDTO.getMedia() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "media is required");
        }

        boolean hasInvalidMedia = requestDTO.getMedia().stream()
                .anyMatch(media -> media.getMediaId() == null || media.getIsCover() == null);

        if (hasInvalidMedia) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mediaId and isCover are required");
        }
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

        List<BookMediaResponseDTO> mediaDTOs = book.getMedia() != null
                ? book.getMedia().stream()
                .map(bookMedia -> BookMediaResponseDTO.builder()
                        .id(bookMedia.getMediaId())
                        .isCover(bookMedia.getIsCover())
                        .order(bookMedia.getOrder())
                        .url(bookMedia.getMediaUrl())
                        .altText(bookMedia.getMediaAltText())
                        .build())
                .toList()
                : List.of();

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
                .media(mediaDTOs)
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .deletedAt(book.getDeletedAt())
                .build();
    }
}