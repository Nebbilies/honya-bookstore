package com.honya.bookstore.catalog.contract;

import com.honya.bookstore.catalog.application.BookSearchCriteria;
import com.honya.bookstore.catalog.application.BookService;
import com.honya.bookstore.catalog.application.CategoryService;
import com.honya.bookstore.catalog.domain.Book;
import com.honya.bookstore.catalog.domain.Category;
import com.honya.bookstore.catalog.web.BookController;
import com.honya.bookstore.catalog.web.CategoryController;
import com.honya.bookstore.catalog.web.dto.request.CategoryRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FrontendCatalogContractTest {

    private MockMvc mockMvc;
    private BookService bookService;
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        bookService = mock(BookService.class);
        categoryService = mock(CategoryService.class);
        BookController bookController = new BookController(bookService, categoryService);
        CategoryController categoryController = new CategoryController(categoryService);
        mockMvc = MockMvcBuilders.standaloneSetup(bookController, categoryController).build();
    }

    @Test
    void getBooksReturns200AndExpectedFields() throws Exception {
        Category category = Category.builder()
                .id(UUID.randomUUID())
                .name("Fiction")
                .slug("fiction")
                .description("Fiction books")
                .build();

        Book book = Book.builder()
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
                .categories(List.of(category))
                .build();

        Page<Book> page = new PageImpl<>(List.of(book), PageRequest.of(0, 10), 1);
        when(bookService.getAllBooks(any(BookSearchCriteria.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.meta.currentPage").value(1))
                .andExpect(jsonPath("$.meta.totalItems").value(1))
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].title").value("Book A"))
                .andExpect(jsonPath("$.data[0].author").value("Author"))
                .andExpect(jsonPath("$.data[0].price").value(100))
                .andExpect(jsonPath("$.data[0].stockQuantity").value(10))
                .andExpect(jsonPath("$.data[0].rating").value(4.5))
                .andExpect(jsonPath("$.data[0].categories[0].name").value("Fiction"))
                .andExpect(jsonPath("$.data[0].categories[0].slug").value("fiction"));
    }

    @Test
    void getBookByIdInvalidUuidReturns400() throws Exception {
        mockMvc.perform(get("/api/books/not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBookReturns201AndExpectedFields() throws Exception {
        UUID categoryId = UUID.randomUUID();
        Category category = Category.builder().id(categoryId).name("Fiction").slug("fiction").description("Fiction books").build();
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);

        Book saved = Book.builder()
                .id(UUID.randomUUID())
                .title("New")
                .author("Writer")
                .price(150)
                .categories(List.of(category))
                .build();
        when(bookService.createBook(any(Book.class), anyList())).thenReturn(saved);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookRequestJson(categoryId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("New"))
                .andExpect(jsonPath("$.categories[0].id").exists());
    }

    @Test
    void deleteBookReturns204() throws Exception {
        UUID bookId = UUID.randomUUID();
        doNothing().when(bookService).deleteBook(bookId);

        mockMvc.perform(delete("/api/books/{id}", bookId))
                .andExpect(status().isNoContent());
    }

    @Test
    void getCategoriesReturns200AndExpectedFields() throws Exception {
        Category category = Category.builder()
                .id(UUID.randomUUID())
                .name("Cat")
                .slug("cat")
                .description("Cat Desc")
                .build();

        when(categoryService.getAllCategories()).thenReturn(List.of(category));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.meta.currentPage").value(1))
                .andExpect(jsonPath("$.meta.totalItems").value(1))
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].name").value("Cat"))
                .andExpect(jsonPath("$.data[0].slug").value("cat"))
                .andExpect(jsonPath("$.data[0].description").value("Cat Desc"));
    }

    @Test
    void createCategoryReturns201AndExpectedFields() throws Exception {
        Category saved = Category.builder()
                .id(UUID.randomUUID())
                .name("New Cat")
                .slug("new-cat")
                .description("New Cat Desc")
                .build();
        when(categoryService.createCategory(any(Category.class))).thenReturn(saved);

        CategoryRequestDTO request = CategoryRequestDTO.builder()
                .name("New Cat")
                .slug("new-cat")
                .description("New Cat Desc")
                .build();

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New Cat"))
                .andExpect(jsonPath("$.slug").value("new-cat"));
    }

    @Test
    void updateCategoryReturns200() throws Exception {
        UUID categoryId = UUID.randomUUID();
        Category updated = Category.builder().id(categoryId).name("Updated Cat").slug("updated-cat").description("Updated").build();
        when(categoryService.updateCategory(eq(categoryId), any(Category.class))).thenReturn(updated);

        CategoryRequestDTO request = CategoryRequestDTO.builder()
                .name("Updated Cat")
                .slug("updated-cat")
                .description("Updated")
                .build();

        mockMvc.perform(put("/api/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId.toString()));
    }

    private String bookRequestJson(UUID categoryId) {
        return "{"
                + "\"title\":\"New\","
                + "\"description\":\"New Desc\","
                + "\"author\":\"Writer\","
                + "\"price\":150,"
                + "\"pagesCount\":300,"
                + "\"yearPublished\":2021,"
                + "\"publisher\":\"Pub2\","
                + "\"weight\":1.1,"
                + "\"stockQuantity\":20,"
                + "\"categoryIds\":[\"" + categoryId + "\"],"
                + "\"media\":[{\"mediaId\":\"" + UUID.randomUUID() + "\",\"isCover\":true}]"
                + "}";
    }

    private String categoryJson(CategoryRequestDTO request) {
        return "{"
                + "\"name\":\"" + request.getName() + "\","
                + "\"slug\":\"" + request.getSlug() + "\","
                + "\"description\":\"" + request.getDescription() + "\""
                + "}";
    }
}
