package com.honya.bookstore.contract;

import com.honya.bookstore.catalog.application.BookSearchCriteria;
import com.honya.bookstore.catalog.application.BookService;
import com.honya.bookstore.catalog.application.CategoryService;
import com.honya.bookstore.catalog.domain.Book;
import com.honya.bookstore.catalog.domain.Category;
import com.honya.bookstore.catalog.web.BookController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FrontendBooksContractTest {

    private MockMvc mockMvc;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = mock(BookService.class);
        CategoryService categoryService = mock(CategoryService.class);
        BookController bookController = new BookController(bookService, categoryService);

        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    void getBooks_returns_data_meta_and_book_media_fields() throws Exception {
        Page<Book> page = new PageImpl<>(List.of(sampleBook()), PageRequest.of(0, 10), 1);
        when(bookService.getAllBooks(any(BookSearchCriteria.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/books?page=1&limit=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.meta.totalItems").exists())
                .andExpect(jsonPath("$.data[0].media").isArray())
                .andExpect(jsonPath("$.data[0].createdAt").exists())
                .andExpect(jsonPath("$.data[0].deletedAt").exists());
    }

    @Test
    void getBooks_returns_frontend_pagination_meta_values() throws Exception {
        Page<Book> page = new PageImpl<>(List.of(sampleBook()), PageRequest.of(0, 10), 1);
        when(bookService.getAllBooks(any(BookSearchCriteria.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/books?page=1&limit=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.currentPage").value(1))
                .andExpect(jsonPath("$.meta.itemsPerPage").value(10))
                .andExpect(jsonPath("$.meta.totalPages").isNumber())
                .andExpect(jsonPath("$.meta.totalItems").isNumber());
    }

    @Test
    void getBooks_accepts_filter_query_params() throws Exception {
        Page<Book> page = new PageImpl<>(List.of(sampleBook()), PageRequest.of(0, 10), 1);
        when(bookService.getAllBooks(any(BookSearchCriteria.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/books?page=1&limit=10&year=2020&sort_price=asc&sort_rating=desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.currentPage").value(1));
    }

    @Test
    void getBooks_returns_400_when_min_price_gt_max_price() throws Exception {
        mockMvc.perform(get("/api/books?min_price=200&max_price=100"))
                .andExpect(status().isBadRequest());
    }

    private Book sampleBook() {
        Category category = Category.builder()
                .id(UUID.randomUUID())
                .name("Fiction")
                .slug("fiction")
                .description("Fiction books")
                .createdAt(OffsetDateTime.parse("2026-05-04T10:15:30Z"))
                .updatedAt(OffsetDateTime.parse("2026-05-04T10:15:30Z"))
                .deletedAt(OffsetDateTime.parse("2026-05-04T10:15:30Z"))
                .build();

        return Book.builder()
                .id(UUID.randomUUID())
                .title("Book A")
                .description("Desc")
                .author("Author")
                .price(100)
                .pagesCount(200)
                .yearPublished(2020)
                .publisher("Pub")
                .weight(1.5f)
                .stockQuantity(10)
                .purchaseCount(5)
                .rating(4.5f)
                .createdAt(OffsetDateTime.parse("2026-05-04T10:15:30Z"))
                .updatedAt(OffsetDateTime.parse("2026-05-04T10:15:30Z"))
                .deletedAt(OffsetDateTime.parse("2026-05-04T10:15:30Z"))
                .categories(List.of(category))
                .build();
    }
}
