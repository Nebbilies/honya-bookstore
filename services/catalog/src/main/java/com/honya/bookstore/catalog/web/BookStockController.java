package com.honya.bookstore.catalog.web;

import com.honya.bookstore.catalog.application.BookService;
import com.honya.bookstore.catalog.web.dto.request.StockReservationRequestDTO;
import com.honya.bookstore.security.CustomerOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Stock", description = "Internal stock reservation commands for the checkout saga")
@CustomerOnly
@RestController
@RequestMapping("/api/books/{id}")
@RequiredArgsConstructor
public class BookStockController {

    private final BookService bookService;

    @Operation(summary = "Reserve stock", description = "Reserve stock for a saga; idempotent per (saga, book)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock reserved"),
            @ApiResponse(responseCode = "409", description = "Insufficient stock",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/reserve")
    public ResponseEntity<Void> reserve(@PathVariable UUID id, @RequestBody StockReservationRequestDTO request) {
        bookService.reserveStock(request.getSagaId(), id, request.getQuantity());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Release stock", description = "Release a stock reservation for a saga; idempotent per (saga, book)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock released")
    })
    @PostMapping("/release")
    public ResponseEntity<Void> release(@PathVariable UUID id, @RequestBody StockReservationRequestDTO request) {
        bookService.releaseStock(request.getSagaId(), id, request.getQuantity());
        return ResponseEntity.ok().build();
    }
}
