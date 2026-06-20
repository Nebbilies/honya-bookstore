package com.honya.bookstore.review.web;

import com.honya.bookstore.review.application.ReviewService;
import com.honya.bookstore.review.application.ReviewVoteResult;
import com.honya.bookstore.review.domain.Review;
import com.honya.bookstore.review.web.dto.ReviewRequestDTO;
import com.honya.bookstore.review.web.dto.ReviewResponseDTO;
import com.honya.bookstore.review.web.dto.VoteRequestDTO;
import com.honya.bookstore.review.web.dto.VoteResponseDTO;
import com.honya.bookstore.security.CustomerOnly;
import com.honya.bookstore.shared.PageMetaDTO;
import com.honya.bookstore.shared.PagedResponseDTO;
import com.honya.bookstore.shared.error.InvalidReviewException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Reviews", description = "Endpoints for book reviews and review votes")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "List reviews", description = "List a book's reviews with the caller's vote state")
    @GetMapping
    public ResponseEntity<PagedResponseDTO<ReviewResponseDTO>> getReviews(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam UUID bookId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Page<Review> reviews = reviewService.getReviewsByBook(bookId, buildPageable(page, limit));

        UUID userId = jwt == null ? null : UUID.fromString(jwt.getSubject());
        Map<UUID, Boolean> userVotes = reviewService.getUserVotes(
                userId, reviews.getContent().stream().map(Review::getId).toList());

        return ResponseEntity.ok(toPagedResponse(reviews, userVotes));
    }

    @Operation(summary = "Create or update review", description = "Submit the caller's review for a book")
    @CustomerOnly
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody ReviewRequestDTO request) {
        UUID userId = UUID.fromString(jwt.getSubject());
        Review review = reviewService.createOrUpdateReview(
                userId, resolveAuthorName(jwt), request.getBookId(), request.getRating(), request.getContent());
        return ResponseEntity.ok(toResponse(review, null));
    }

    @Operation(summary = "Delete review", description = "Delete the caller's own review")
    @CustomerOnly
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id) {
        reviewService.deleteReview(id, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Vote on review", description = "Toggle an up/down vote on a review")
    @CustomerOnly
    @PostMapping("/{id}/vote")
    public ResponseEntity<VoteResponseDTO> vote(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @RequestBody VoteRequestDTO request) {
        boolean isUp = parseVote(request.getValue());
        ReviewVoteResult result = reviewService.vote(id, UUID.fromString(jwt.getSubject()), isUp);
        return ResponseEntity.ok(new VoteResponseDTO(result.voteCount(), result.userVote()));
    }

    private String resolveAuthorName(Jwt jwt) {
        String name = jwt.getClaimAsString("name");
        if (name != null && !name.isBlank()) {
            return name;
        }
        String username = jwt.getClaimAsString("preferred_username");
        return username != null && !username.isBlank() ? username : "Anonymous";
    }

    private boolean parseVote(String value) {
        if ("UP".equalsIgnoreCase(value)) {
            return true;
        }
        if ("DOWN".equalsIgnoreCase(value)) {
            return false;
        }
        throw new InvalidReviewException("Vote value must be UP or DOWN");
    }

    private Pageable buildPageable(int page, int limit) {
        int safePage = Math.max(page, 1);
        int safeLimit = Math.max(limit, 1);
        return PageRequest.of(safePage - 1, safeLimit, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private PagedResponseDTO<ReviewResponseDTO> toPagedResponse(Page<Review> page, Map<UUID, Boolean> userVotes) {
        List<ReviewResponseDTO> data = page.getContent().stream()
                .map(review -> toResponse(review, userVotes.get(review.getId())))
                .toList();
        PageMetaDTO meta = new PageMetaDTO(
                page.getNumber() + 1,
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalElements(),
                page.getTotalPages()
        );
        return new PagedResponseDTO<>(data, meta);
    }

    private ReviewResponseDTO toResponse(Review review, Boolean isUp) {
        String userVote = isUp == null ? null : (isUp ? "UP" : "DOWN");
        return new ReviewResponseDTO(
                review.getId(),
                review.getBookId(),
                review.getAuthorId(),
                review.getAuthorName(),
                review.getRating(),
                review.getContent(),
                review.getVoteCount(),
                userVote,
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
