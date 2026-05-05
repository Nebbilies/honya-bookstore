package com.honya.bookstore.contract;

import com.honya.bookstore.catalog.controller.BookController;
import com.honya.bookstore.catalog.service.BookService;
import com.honya.bookstore.catalog.service.CategoryService;
import com.honya.bookstore.shared.error.InsufficientStockException;
import com.honya.bookstore.shared.error.ResourceNotFoundException;
import com.honya.bookstore.shared.web.ProblemDetailsHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProblemDetailsContractTest {

    private MockMvc mockMvc;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = mock(BookService.class);
        CategoryService categoryService = mock(CategoryService.class);
        BookController bookController = new BookController(bookService, categoryService);

        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setControllerAdvice(new ProblemDetailsHandler())
                .build();
    }

    @Test
    void resourceNotFoundReturnsProblemDetails404() throws Exception {
        UUID bookId = UUID.randomUUID();
        when(bookService.getBookById(bookId)).thenThrow(new ResourceNotFoundException("Book", bookId));

        mockMvc.perform(get("/api/books/{id}", bookId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.detail").value("Book not found with ID: " + bookId))
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void invalidUuidReturnsProblemDetails400() throws Exception {
        mockMvc.perform(get("/api/books/not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad request"))
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void insufficientStockReturnsProblemDetails409() throws Exception {
        UUID bookId = UUID.randomUUID();
        when(bookService.getBookById(bookId)).thenThrow(new InsufficientStockException(bookId, "Demo Book", 5, 2));

        mockMvc.perform(get("/api/books/{id}", bookId))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Insufficient stock"))
                .andExpect(jsonPath("$.detail").value("Insufficient stock for book Demo Book: requested 5, available 2"))
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_STOCK"));
    }
}
