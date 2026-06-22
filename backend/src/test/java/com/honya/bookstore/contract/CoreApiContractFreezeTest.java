package com.honya.bookstore.contract;

import com.honya.bookstore.checkout.application.CheckoutService;
import com.honya.bookstore.checkout.web.CheckoutController;
import com.honya.bookstore.checkout.web.dto.CheckoutRequestDTO;
import com.honya.bookstore.order.api.OrderItemResponse;
import com.honya.bookstore.order.api.OrderResponse;
import com.honya.bookstore.order.domain.Order;
import com.honya.bookstore.order.web.OrderController;
import com.honya.bookstore.order.domain.OrderItem;
import com.honya.bookstore.order.application.OrderService;
import com.honya.bookstore.order.domain.OrderProvider;
import com.honya.bookstore.order.infrastructure.payment.VnPayUrlBuilder;
import com.honya.bookstore.order.domain.OrderStatus;
import com.honya.bookstore.order.domain.OrderItemBook;
import com.honya.bookstore.shared.integration.cart.CartClient;
import com.honya.bookstore.shared.integration.catalog.CatalogClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CoreApiContractFreezeTest {

    private MockMvc mockMvc;
    private OrderService orderService;
    private CheckoutService checkoutService;

    @BeforeEach
    void setUp() {
        orderService = mock(OrderService.class);
        checkoutService = mock(CheckoutService.class);

        CartClient cartClient = mock(CartClient.class);
        CatalogClient catalogClient = mock(CatalogClient.class);
        VnPayUrlBuilder vnPayUrlBuilder = mock(VnPayUrlBuilder.class);
        OrderController orderController = new OrderController(orderService, cartClient, catalogClient, vnPayUrlBuilder);
        CheckoutController checkoutController = new CheckoutController(checkoutService);

        mockMvc = MockMvcBuilders.standaloneSetup(
                orderController,
                checkoutController
        ).setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver()).build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // Simulates an authenticated request by placing a JWT (subject = userId) in the
    // security context, matching the OAuth2 resource-server contract the controllers use.
    private void authenticate(String userId) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(userId)
                .build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }

    @Test
    void getOrdersWithHeaderReturns200AndExpectedFields() throws Exception {
        String userId = UUID.randomUUID().toString();
        Order order = sampleOrder(UUID.fromString(userId));

        when(orderService.getOrdersByUserId(eq(userId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(order)));
        authenticate(userId);

        mockMvc.perform(get("/api/orders/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].firstName").value("John"))
                .andExpect(jsonPath("$.data[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.data[0].address").value("Street 1"))
                .andExpect(jsonPath("$.data[0].city").value("City"))
                .andExpect(jsonPath("$.data[0].provider").value("COD"))
                .andExpect(jsonPath("$.data[0].status").value("PENDING"))
                .andExpect(jsonPath("$.data[0].isPaid").value(false))
                .andExpect(jsonPath("$.data[0].totalAmount").value(1234))
                .andExpect(jsonPath("$.data[0].userId").value(userId))
                .andExpect(jsonPath("$.data[0].items[0].id").exists())
                .andExpect(jsonPath("$.data[0].items[0].book.id").exists())
                .andExpect(jsonPath("$.data[0].items[0].quantity").value(2))
                .andExpect(jsonPath("$.data[0].items[0].price").value(617))
                .andExpect(jsonPath("$.meta.totalItems").value(1));
    }

    @Test
    void getOrderByIdReturns200() throws Exception {
        UUID orderId = UUID.randomUUID();
        Order order = sampleOrder(UUID.randomUUID());
        order.setId(orderId);

        when(orderService.getOrderById(orderId)).thenReturn(order);

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()));
    }

    @Test
    void getOrderByIdInvalidUuidReturns400() throws Exception {
        mockMvc.perform(get("/api/orders/not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkoutWithHeaderReturns200AndExpectedFields() throws Exception {
        String userId = UUID.randomUUID().toString();
        OrderResponse created = sampleOrderResponse(UUID.fromString(userId));

        when(checkoutService.checkout(eq(userId), any(CheckoutRequestDTO.class))).thenReturn(created);
        authenticate(userId);

        CheckoutRequestDTO request = new CheckoutRequestDTO();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setAddress("Street 1");
        request.setCity("City");

        mockMvc.perform(post("/api/checkout/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.address").value("Street 1"))
                .andExpect(jsonPath("$.city").value("City"))
                .andExpect(jsonPath("$.provider").value("COD"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.isPaid").value(false))
                .andExpect(jsonPath("$.totalAmount").value(1234))
                .andExpect(jsonPath("$.userId").value(userId));
    }

    private String toJson(Object source) throws IllegalAccessException {
        StringBuilder sb = new StringBuilder("{");
        Field[] fields = source.getClass().getDeclaredFields();
        int written = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(source);
            if (written > 0) {
                sb.append(",");
            }
            sb.append("\"").append(field.getName()).append("\":");
            if (value == null) {
                sb.append("null");
            } else if (value instanceof Number || value instanceof Boolean) {
                sb.append(value);
            } else {
                sb.append("\"").append(value.toString().replace("\"", "\\\"")).append("\"");
            }
            written++;
        }
        sb.append("}");
        return sb.toString();
    }

    private OrderResponse sampleOrderResponse(UUID userId) {
        return new OrderResponse(
                UUID.randomUUID(),
                "John",
                "Doe",
                "Street 1",
                "City",
                "john@example.com",
                "+84912345678",
                null,
                "COD",
                "PENDING",
                false,
                1234,
                userId,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                List.of(new OrderItemResponse(UUID.randomUUID(), UUID.randomUUID(), 2, 617))
        );
    }

    private Order sampleOrder(UUID userId) {
        OrderItem item = OrderItem.builder()
                .id(UUID.randomUUID())
                .book(OrderItemBook.builder().id(UUID.randomUUID()).build())
                .quantity(2)
                .price(617)
                .build();

        return Order.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .address("Street 1")
                .city("City")
                .provider(OrderProvider.COD)
                .status(OrderStatus.PENDING)
                .isPaid(false)
                .totalAmount(1234)
                .userId(userId)
                .items(List.of(item))
                .build();
    }
}
