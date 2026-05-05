package com.honya.bookstore.catalog.service.impl;

import com.honya.bookstore.catalog.domain.Category;
import com.honya.bookstore.catalog.repo.CategoryRepository;
import com.honya.bookstore.catalog.service.CategoryService;
import com.honya.bookstore.shared.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(UUID id, Category category) {
        Category existingCategory = getCategoryById(id);

        existingCategory.setName(category.getName());
        existingCategory.setSlug(category.getSlug());
        existingCategory.setDescription(category.getDescription());

        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }
}