package com.honya.bookstore.checkout.web;

import com.honya.bookstore.checkout.application.CheckoutService;
import com.honya.bookstore.checkout.web.dto.CheckoutRequestDTO;
import com.honya.bookstore.order.api.OrderResponse;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Checkout", description = "Endpoints for processing the checkout of the shopping cart and placing orders")
@CustomerOnly
@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @Operation(summary = "Checkout cart", description = "Process user cart checkout and create order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checkout completed"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Cart or book not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CheckoutRequestDTO request) {
        String userId  = jwt.getSubject();
        return ResponseEntity.ok(checkoutService.checkout(userId, request));
    }
}
