package com.honya.bookstore.contract;

import com.honya.bookstore.catalog.application.CategoryService;
import com.honya.bookstore.catalog.domain.Category;
import com.honya.bookstore.catalog.web.CategoryController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FrontendCategoriesContractTest {

    private MockMvc mockMvc;
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = mock(CategoryService.class);
        CategoryController categoryController = new CategoryController(categoryService);

        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

    @Test
    void getCategories_returns_data_meta_and_timestamp_fields() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of(sampleCategory()));

        mockMvc.perform(get("/api/categories?page=1&limit=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.meta.totalItems").exists())
                .andExpect(jsonPath("$.data[0].createdAt").exists())
                .andExpect(jsonPath("$.data[0].updatedAt").exists())
                .andExpect(jsonPath("$.data[0].deletedAt").exists());
    }

    @Test
    void getCategoryBySlug_returns_category_using_slug_route() throws Exception {
        Category category = sampleCategory();
        when(categoryService.getCategoryBySlug(category.getSlug())).thenReturn(category);

        mockMvc.perform(get("/api/categories/slug/{slug}", category.getSlug()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(category.getId().toString()))
                .andExpect(jsonPath("$.slug").value("fiction"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.deletedAt").exists());
    }

    private Category sampleCategory() {
        return Category.builder()
                .id(UUID.randomUUID())
                .name("Fiction")
                .slug("fiction")
                .description("Fiction books")
                .createdAt(OffsetDateTime.parse("2026-05-04T10:15:30Z"))
                .updatedAt(OffsetDateTime.parse("2026-05-04T10:15:30Z"))
                .deletedAt(OffsetDateTime.parse("2026-05-04T10:15:30Z"))
                .build();
    }
}
