package com.honya.bookstore.contract;

import com.honya.bookstore.checkout.application.CheckoutService;
import com.honya.bookstore.checkout.web.CheckoutController;
import com.honya.bookstore.checkout.web.dto.CheckoutRequestDTO;
import com.honya.bookstore.shared.integration.order.OrderItemResponse;
import com.honya.bookstore.shared.integration.order.OrderResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CoreApiContractFreezeTest {

    private MockMvc mockMvc;
    private CheckoutService checkoutService;

    @BeforeEach
    void setUp() {
        checkoutService = mock(CheckoutService.class);
        CheckoutController checkoutController = new CheckoutController(checkoutService);

        mockMvc = MockMvcBuilders.standaloneSetup(checkoutController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticate(String userId) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(userId)
                .build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
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
}
