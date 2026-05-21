package com.honya.bookstore.contract;

import com.honya.bookstore.order.application.OrderService;
import com.honya.bookstore.order.domain.Order;
import com.honya.bookstore.order.domain.OrderItem;
import com.honya.bookstore.order.domain.OrderProvider;
import com.honya.bookstore.order.domain.OrderStatus;
import com.honya.bookstore.order.domain.OrderItemBook;
import com.honya.bookstore.order.web.OrderController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FrontendOrdersContractTest {

    private MockMvc mockMvc;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = mock(OrderService.class);
        OrderController orderController = new OrderController(orderService);

        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    void getOrders_returns_data_meta_and_frontend_order_fields() throws Exception {
        String userId = UUID.randomUUID().toString();
        when(orderService.getOrdersByUserId(userId)).thenReturn(List.of(sampleOrder(UUID.fromString(userId))));

        mockMvc.perform(get("/api/orders?page=1&limit=10")
                        .header("X-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.meta.totalItems").exists())
                .andExpect(jsonPath("$.data[0].phone").exists())
                .andExpect(jsonPath("$.data[0].email").exists())
                .andExpect(jsonPath("$.data[0].paymentUrl").exists())
                .andExpect(jsonPath("$.data[0].createdAt").exists());
    }

    @Test
    void getOrderById_returns_frontend_order_fields() throws Exception {
        UUID orderId = UUID.randomUUID();
        Order order = sampleOrder(UUID.randomUUID());
        order.setId(orderId);
        when(orderService.getOrderById(orderId)).thenReturn(order);

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.phone").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.paymentUrl").exists())
                .andExpect(jsonPath("$.createdAt").exists());
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
                .phone("0123456789")
                .email("john@example.com")
                .paymentUrl("http://localhost:3000/payment/mock")
                .createdAt(OffsetDateTime.parse("2026-05-04T10:15:30Z"))
                .provider(OrderProvider.COD)
                .status(OrderStatus.PENDING)
                .isPaid(false)
                .totalAmount(1234)
                .userId(userId)
                .items(List.of(item))
                .build();
    }
}
