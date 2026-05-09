package com.honya.bookstore.catalog.web;

import com.honya.bookstore.catalog.domain.Category;
import com.honya.bookstore.catalog.web.dto.request.CategoryRequestDTO;
import com.honya.bookstore.catalog.web.dto.response.CategoryResponseDTO;

import com.honya.bookstore.catalog.application.CategoryService;
import com.honya.bookstore.security.CustomerOnly;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Categories", description = "Endpoints for managing book categories in the catalog")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get all categories", description = "Retrieve all categories in catalog")
    @CustomerOnly
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved")
    })
    @GetMapping
    public ResponseEntity<PagedResponseDTO<CategoryResponseDTO>> getAllCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        List<CategoryResponseDTO> categories = categoryService.getAllCategories().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        int safePage = Math.max(page, 1);
        int safeLimit = Math.max(limit, 1);
        int totalItems = categories.size();
        int fromIndex = Math.min((safePage - 1) * safeLimit, totalItems);
        int toIndex = Math.min(fromIndex + safeLimit, totalItems);
        List<CategoryResponseDTO> pageData = categories.subList(fromIndex, toIndex);
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

    @Operation(summary = "Get category by id", description = "Retrieve one category by id")
    @CustomerOnly
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category retrieved"),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable UUID id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(mapToResponseDTO(category));
    }

    @CustomerOnly
    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoryResponseDTO> getCategoryBySlug(@PathVariable String slug) {
        Category category = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(mapToResponseDTO(category));
    }

    @Operation(summary = "Create category", description = "Create new category in catalog")
    @StaffOrAdmin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
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

    @Operation(summary = "Update category", description = "Update existing category by id")
    @StaffOrAdmin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
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

    @Operation(summary = "Delete category", description = "Delete category by id")
    @StaffOrAdmin
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted"),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
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
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .deletedAt(category.getDeletedAt())
                .build();
    }
}