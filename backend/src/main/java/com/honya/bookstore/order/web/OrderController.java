package com.honya.bookstore.order.web;

import com.honya.bookstore.order.application.OrderService;
import com.honya.bookstore.order.domain.Order;
import com.honya.bookstore.security.CustomerOnly;
import com.honya.bookstore.shared.PageMetaDTO;
import com.honya.bookstore.shared.PagedResponseDTO;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Orders", description = "Endpoints for managing user orders and retrieving order history")
@CustomerOnly
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Get my orders", description = "Retrieve all orders for authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved")
    })
    @GetMapping
    public ResponseEntity<PagedResponseDTO<Order>> getMyOrders(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        String userId =  jwt.getSubject();
        List<Order> orders = orderService.getOrdersByUserId(userId);

        int safePage = Math.max(page, 1);
        int safeLimit = Math.max(limit, 1);
        int totalItems = orders.size();
        int fromIndex = Math.min((safePage - 1) * safeLimit, totalItems);
        int toIndex = Math.min(fromIndex + safeLimit, totalItems);
        List<Order> pageData = orders.subList(fromIndex, toIndex);
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

    @Operation(summary = "Get order by id", description = "Retrieve one order by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved"),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
}