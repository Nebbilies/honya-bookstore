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
import com.honya.bookstore.shared.PageMetaDTO;
import com.honya.bookstore.shared.PagedResponseDTO;
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
@CustomerOnly
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

    @Operation(summary = "Get my orders", description = "Retrieve all orders for authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved")
    })
    @GetMapping
    public ResponseEntity<PagedResponseDTO<Order>> getMyOrders(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        String userId = jwt.getSubject();
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
