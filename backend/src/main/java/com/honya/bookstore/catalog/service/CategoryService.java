package com.honya.bookstore.catalog.service;

import com.honya.bookstore.catalog.domain.Category;
import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(UUID id);
    Category createCategory(Category category);
    Category updateCategory(UUID id, Category category);
    void deleteCategory(UUID id);
}