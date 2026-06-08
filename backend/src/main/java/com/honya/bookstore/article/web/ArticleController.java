package com.honya.bookstore.article.web;

import com.honya.bookstore.article.application.ArticleService;
import com.honya.bookstore.article.domain.Article;
import com.honya.bookstore.article.domain.ArticleStatus;
import com.honya.bookstore.article.web.dtos.request.ArticleRequestDTO;
import com.honya.bookstore.article.web.dtos.response.ArticleResponseDTO;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Articles", description = "Endpoints for managing articles in the bookstore")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "Get all articles", description = "Retrieve a paginated list of articles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Articles retrieved")
    })
    @StaffOrAdmin
    @GetMapping
    public ResponseEntity<PagedResponseDTO<ArticleResponseDTO>> getAllArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        int safePage = Math.max(page, 1);
        int safeLimit =  10;

        Pageable pageable = PageRequest.of(safePage - 1, safeLimit);
        Page<Article> articlePage = articleService.getAllArticles(pageable);

        PageMetaDTO meta = new PageMetaDTO(
                safePage,
                safeLimit,
                articlePage.getNumberOfElements(),
                articlePage.getTotalElements(),
                articlePage.getTotalPages()
        );

        return ResponseEntity.ok(new PagedResponseDTO<>(
                articlePage.getContent().stream()
                        .map(this::mapToResponseDTO)
                        .collect(Collectors.toList()),
                meta
        ));
    }

    @Operation(summary = "Get article by id", description = "Retrieve one article by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article retrieved"),
            @ApiResponse(responseCode = "404", description = "Article not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @StaffOrAdmin
    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponseDTO> getArticleById(@PathVariable UUID id) {
        Article article = articleService.getArticleById(id);
        return ResponseEntity.ok(mapToResponseDTO(article));
    }

    @Operation(summary = "Create article", description = "Create a new article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Article created"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @StaffOrAdmin
    @PostMapping
    public ResponseEntity<ArticleResponseDTO> createArticle(@RequestBody ArticleRequestDTO requestDto, @AuthenticationPrincipal Jwt jwt) {
        String authorId = jwt.getSubject();
        Article article = Article.builder()
                .slug(requestDto.getSlug())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .tags(requestDto.getTags() == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(requestDto.getTags())))
                .authorId(UUID.fromString(authorId))
                .thumbnailId(requestDto.getThumbnailId())
                .thumbnailUrl(requestDto.getThumbnailUrl())
                .status(parseStatus(requestDto.getStatus()))
                .build();

        Article savedArticle = articleService.createArticle(article);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDTO(savedArticle));
    }

    @Operation(summary = "Update article", description = "Update an existing article by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Article not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @StaffOrAdmin
    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponseDTO> updateArticle(
            @PathVariable UUID id,
            @RequestBody ArticleRequestDTO requestDto,
            @AuthenticationPrincipal Jwt jwt
            ) {
        String authorId = jwt.getSubject();
        Article articleDetails = Article.builder()
                .slug(requestDto.getSlug())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .tags(requestDto.getTags() == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(requestDto.getTags())))
                .authorId(UUID.fromString(authorId))
                .thumbnailId(requestDto.getThumbnailId())
                .thumbnailUrl(requestDto.getThumbnailUrl())
                .status(parseStatus(requestDto.getStatus()))
                .build();
        Article savedArticle = articleService.updateArticle(id, articleDetails);
        return ResponseEntity.ok(mapToResponseDTO(savedArticle));
    }

    @Operation(summary = "Delete article", description = "Delete an article by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Article deleted"),
            @ApiResponse(responseCode = "404", description = "Article not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @StaffOrAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable UUID id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }

    private ArticleStatus parseStatus(String status) {
        if (status == null) {
            throw new IllegalArgumentException("Article status is required");
        }
        try {
            return ArticleStatus.valueOf(status);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid article status: " + status);
        }
    }

    private ArticleResponseDTO mapToResponseDTO(Article article) {
        return ArticleResponseDTO.builder()
                .id(article.getId())
                .slug(article.getSlug())
                .title(article.getTitle())
                .content(article.getContent())
                .tags(article.getTags() == null ? new String[0] : article.getTags().toArray(new String[0]))
                .authorId(article.getAuthorId())
                .thumbnailId(article.getThumbnailId())
                .thumbnailUrl(article.getThumbnailUrl())
                .status(String.valueOf(article.getStatus()))
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .deletedAt(article.getDeletedAt())
                .build();
    }
}
