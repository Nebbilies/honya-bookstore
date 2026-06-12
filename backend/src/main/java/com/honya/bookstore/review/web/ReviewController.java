package com.honya.bookstore.review.web;

import com.honya.bookstore.review.application.ReviewService;
import com.honya.bookstore.review.domain.Review;
import com.honya.bookstore.review.web.dto.request.ReviewRequestDTO;
import com.honya.bookstore.review.web.dto.request.ReviewVoteRequestDTO;
import com.honya.bookstore.review.web.dto.response.ReviewResponseDTO;
import com.honya.bookstore.security.CustomerOnly;
import com.honya.bookstore.shared.PageMetaDTO;
import com.honya.bookstore.shared.PagedResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Reviews", description = "Endpoints for managing book reviews")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Get reviews", description = "Retrieve reviews, optionally filtered by book id")
    @GetMapping
    public ResponseEntity<PagedResponseDTO<ReviewResponseDTO>> getReviews(
            @RequestParam(required = false) UUID bookId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        int safePage = Math.max(page, 1);
        int safeLimit = Math.max(limit, 1);

        Page<Review> reviewPage = reviewService.getReviews(bookId, PageRequest.of(safePage - 1, safeLimit));
        List<ReviewResponseDTO> pageData = reviewPage.getContent().stream()
                .map(this::mapToResponseDTO)
                .toList();

        PageMetaDTO meta = new PageMetaDTO(
                safePage,
                safeLimit,
                pageData.size(),
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages()
        );

        return ResponseEntity.ok(new PagedResponseDTO<>(pageData, meta));
    }

    @Operation(summary = "Get review by id", description = "Retrieve one review by id")
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> getReviewById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapToResponseDTO(reviewService.getReviewById(id)));
    }

    @Operation(summary = "Create review", description = "Create a review for the authenticated user")
    @CustomerOnly
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody ReviewRequestDTO request) {
        Review review = reviewService.createReview(jwt.getSubject(), request.getBookId(), request.getRating(), request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDTO(review));
    }

    @Operation(summary = "Update review", description = "Update an authenticated user's own review")
    @CustomerOnly
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @RequestBody ReviewRequestDTO request) {
        Review review = reviewService.updateReview(jwt.getSubject(), id, request.getRating(), request.getContent());
        return ResponseEntity.ok(mapToResponseDTO(review));
    }

    @Operation(summary = "Patch review", description = "Update an authenticated user's own review")
    @CustomerOnly
    @PatchMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> patchReview(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @RequestBody ReviewRequestDTO request) {
        Review review = reviewService.updateReview(jwt.getSubject(), id, request.getRating(), request.getContent());
        return ResponseEntity.ok(mapToResponseDTO(review));
    }

    @Operation(summary = "Delete review", description = "Delete an authenticated user's own review")
    @CustomerOnly
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id) {
        reviewService.deleteReview(jwt.getSubject(), id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Vote review", description = "Create or update the authenticated user's vote on a review")
    @CustomerOnly
    @PostMapping("/{id}/vote")
    public ResponseEntity<ReviewResponseDTO> voteReview(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @RequestBody ReviewVoteRequestDTO request) {
        Review review = reviewService.voteReview(jwt.getSubject(), id, request.getIsUp());
        return ResponseEntity.ok(mapToResponseDTO(review));
    }

    @Operation(summary = "Remove review vote", description = "Remove the authenticated user's vote on a review")
    @CustomerOnly
    @DeleteMapping("/{id}/vote")
    public ResponseEntity<ReviewResponseDTO> removeVote(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id) {
        Review review = reviewService.removeVote(jwt.getSubject(), id);
        return ResponseEntity.ok(mapToResponseDTO(review));
    }

    private ReviewResponseDTO mapToResponseDTO(Review review) {
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .bookId(review.getBookId())
                .userId(review.getAuthorId())
                .rating(review.getRating())
                .voteCount(review.getVoteCount())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
