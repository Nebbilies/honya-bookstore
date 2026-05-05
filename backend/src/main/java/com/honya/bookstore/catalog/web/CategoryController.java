package com.honya.bookstore.catalog.web;

import com.honya.bookstore.catalog.domain.Category;
import com.honya.bookstore.catalog.web.dto.request.CategoryRequestDTO;
import com.honya.bookstore.catalog.web.dto.response.CategoryResponseDTO;

import com.honya.bookstore.catalog.application.CategoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        List<CategoryResponseDTO> categories = categoryService.getAllCategories().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable UUID id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(mapToResponseDTO(category));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO requestDTO) {
        Category category = Category.builder()
                .name(requestDTO.getName())
                .slug(requestDTO.getSlug())
                .description(requestDTO.getDescription())
                .build();

        Category savedCategory = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDTO(savedCategory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable UUID id, @RequestBody CategoryRequestDTO requestDTO) {
        Category categoryDetails = Category.builder()
                .name(requestDTO.getName())
                .slug(requestDTO.getSlug())
                .description(requestDTO.getDescription())
                .build();

        Category updatedCategory = categoryService.updateCategory(id, categoryDetails);
        return ResponseEntity.ok(mapToResponseDTO(updatedCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // Helper method to convert Entity to DTO
    private CategoryResponseDTO mapToResponseDTO(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .build();
    }
}