package com.honya.bookstore.order.web;

import com.honya.bookstore.cart.api.CartApi;
import com.honya.bookstore.catalog.api.CatalogCartSnapshot;
import com.honya.bookstore.catalog.api.CatalogStockApi;
import com.honya.bookstore.order.application.OrderService;
import com.honya.bookstore.order.domain.Order;
import com.honya.bookstore.order.domain.OrderItem;
import com.honya.bookstore.order.domain.OrderItemBook;
import com.honya.bookstore.order.domain.OrderProvider;
import com.honya.bookstore.order.domain.OrderStatus;
import com.honya.bookstore.order.infrastructure.payment.VnPayUrlBuilder;
import com.honya.bookstore.order.web.dto.OrderRequestDTO;
import com.honya.bookstore.security.CustomerOnly;
import com.honya.bookstore.security.StaffOrAdmin;
import com.honya.bookstore.shared.error.InvalidOrderStatusException;
import com.honya.bookstore.shared.error.ResourceNotFoundException;
import com.honya.bookstore.shared.PageMetaDTO;
import com.honya.bookstore.shared.PagedResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Orders", description = "Endpoints for managing user orders and retrieving order history")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CartApi cartApi;
    private final CatalogStockApi catalogStockApi;
    private final VnPayUrlBuilder vnPayUrlBuilder;

    @Operation(summary = "Create order", description = "Create order from authenticated user's cart snapshot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Cart or product not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @CustomerOnly
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody OrderRequestDTO request,
            HttpServletRequest httpServletRequest) {
        String userId = jwt.getSubject();
        OrderProvider provider = request.getProvider() == null
                ? OrderProvider.COD
                : OrderProvider.valueOf(request.getProvider().toUpperCase());

        List<OrderItem> items = cartApi.getCheckoutSnapshot(userId).items().stream()
                .map(item -> {
                    CatalogCartSnapshot snapshot = catalogStockApi.getCartSnapshot(item.bookId());
                    return OrderItem.builder()
                            .book(OrderItemBook.builder()
                                    .id(snapshot.id())
                                    .title(snapshot.title())
                                    .author(snapshot.author())
                                    .price(snapshot.price())
                                    .rating(0)
                                    .build())
                            .quantity(item.quantity())
                            .price(snapshot.price())
                            .build();
                })
                .toList();

        int totalAmount = items.stream()
                .mapToInt(item -> item.getPrice() * item.getQuantity())
                .sum();

        Order order = Order.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .address(request.getAddress())
                .city(request.getCity())
                .email(request.getEmail())
                .phone(request.getPhone())
                .provider(provider)
                .status(provider == OrderProvider.COD ? OrderStatus.PROCESSING : OrderStatus.PENDING)
                .isPaid(Boolean.FALSE)
                .totalAmount(totalAmount)
                .items(items)
                .build();

        Order createdOrder = orderService.createOrder(userId, order);

        if (provider == OrderProvider.VNPAY) {
            String clientIp = extractClientIp(httpServletRequest);
            String paymentUrl = vnPayUrlBuilder.buildPaymentUrl(createdOrder, clientIp, request.getReturnUrl());
            createdOrder = orderService.updatePaymentUrl(createdOrder.getId(), paymentUrl);
        }

        return ResponseEntity.ok(createdOrder);
    }

    @Operation(summary = "Get all orders", description = "Retrieve all orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved")
    })
    @StaffOrAdmin
    @GetMapping
    public ResponseEntity<PagedResponseDTO<Order>> getAllOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {
        Page<Order> orders = orderService.searchOrders(parseStatus(status), search, buildPageable(page, limit));
        return ResponseEntity.ok(toPagedResponse(orders));
    }

    @Operation(summary = "Get my orders", description = "Retrieve all orders for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved")
    })
    @CustomerOnly
    @GetMapping("/me")
    public ResponseEntity<PagedResponseDTO<Order>> getMyOrders(
            @AuthenticationPrincipal(expression = "subject") String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Page<Order> orders = orderService.getOrdersByUserId(userId, buildPageable(page, limit));
        return ResponseEntity.ok(toPagedResponse(orders));
    }

    @Operation(summary = "Get my order by id", description = "Retrieve one of the authenticated user's own orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved"),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @CustomerOnly
    @GetMapping("/me/{id}")
    public ResponseEntity<Order> getMyOrderById(
            @AuthenticationPrincipal(expression = "subject") String userId,
            @PathVariable UUID id) {
        Order order = orderService.getOrderById(id);
        if (!UUID.fromString(userId).equals(order.getUserId())) {
            throw new ResourceNotFoundException("Order", id);
        }
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Get order by id", description = "Retrieve one order by id (staff/admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved"),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @StaffOrAdmin
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    private OrderStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return OrderStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidOrderStatusException(status);
        }
    }

    private Pageable buildPageable(int page, int limit) {
        int safePage = Math.max(page, 1);
        int safeLimit = Math.max(limit, 1);
        return PageRequest.of(safePage - 1, safeLimit, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private PagedResponseDTO<Order> toPagedResponse(Page<Order> page) {
        PageMetaDTO meta = new PageMetaDTO(
                page.getNumber() + 1,
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalElements(),
                page.getTotalPages()
        );
        return new PagedResponseDTO<>(page.getContent(), meta);
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }
        return request.getRemoteAddr();
    }
}
