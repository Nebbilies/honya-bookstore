package com.honya.bookstore.checkout.application;

import com.honya.bookstore.shared.integration.cart.CartClient;
import com.honya.bookstore.shared.integration.cart.CartItemSnapshot;
import com.honya.bookstore.shared.integration.cart.CartSnapshot;
import com.honya.bookstore.shared.integration.catalog.CatalogBookView;
import com.honya.bookstore.shared.integration.catalog.CatalogClient;
import com.honya.bookstore.shared.integration.order.OrderClient;
import com.honya.bookstore.shared.integration.order.OrderItemRequest;
import com.honya.bookstore.shared.integration.order.OrderRequest;
import com.honya.bookstore.shared.integration.order.OrderResponse;
import com.honya.bookstore.checkout.web.dto.CheckoutRequestDTO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CheckoutServiceTest {

    @Test
    void checkoutBuildsOrderItemsFromPublicCartAndCatalogApis() {
        OrderClient orderClient = mock(OrderClient.class);
        CartClient cartClient = mock(CartClient.class);
        CatalogClient catalogClient = mock(CatalogClient.class);
        UUID userId = UUID.randomUUID();
        UUID firstBookId = UUID.randomUUID();
        UUID secondBookId = UUID.randomUUID();
        CheckoutRequestDTO request = new CheckoutRequestDTO();
        request.setFirstName("Ada");
        request.setLastName("Lovelace");
        request.setAddress("12 Example Street");
        request.setCity("London");

        when(cartClient.getCheckoutSnapshot(userId.toString())).thenReturn(new CartSnapshot(userId, List.of(
                new CartItemSnapshot(firstBookId, 2),
                new CartItemSnapshot(secondBookId, 1)
        )));
        when(catalogClient.getBook(firstBookId)).thenReturn(new CatalogBookView(firstBookId, "First", "Author", "img", 100));
        when(catalogClient.getBook(secondBookId)).thenReturn(new CatalogBookView(secondBookId, "Second", "Author", "img", 250));
        OrderResponse response = new OrderResponse(
                UUID.randomUUID(),
                "Ada",
                "Lovelace",
                "12 Example Street",
                "London",
                null,
                null,
                null,
                null,
                "PENDING",
                false,
                450,
                userId,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                List.of()
        );
        when(orderClient.createOrder(org.mockito.ArgumentMatchers.eq(userId.toString()), any(OrderRequest.class))).thenReturn(response);

        OrderResponse createdOrder = new CheckoutService(orderClient, cartClient, catalogClient)
                .checkout(userId.toString(), request);

        ArgumentCaptor<OrderRequest> orderCaptor = ArgumentCaptor.forClass(OrderRequest.class);
        verify(orderClient).createOrder(org.mockito.ArgumentMatchers.eq(userId.toString()), orderCaptor.capture());
        OrderRequest order = orderCaptor.getValue();
        assertSame(response, createdOrder);
        assertEquals("Ada", order.firstName());
        assertEquals("Lovelace", order.lastName());
        assertEquals("12 Example Street", order.address());
        assertEquals("London", order.city());
        assertEquals(450, order.totalAmount());
        assertEquals(2, order.items().size());
        OrderItemRequest firstItem = order.items().get(0);
        OrderItemRequest secondItem = order.items().get(1);
        assertEquals(firstBookId, firstItem.bookId());
        assertEquals(2, firstItem.quantity());
        assertEquals(100, firstItem.price());
        assertEquals(secondBookId, secondItem.bookId());
        assertEquals(1, secondItem.quantity());
        assertEquals(250, secondItem.price());
    }
}
