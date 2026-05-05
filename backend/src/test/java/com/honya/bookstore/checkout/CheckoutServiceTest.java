package com.honya.bookstore.checkout;

import com.honya.bookstore.cart.api.CartApi;
import com.honya.bookstore.cart.api.CartItemSnapshot;
import com.honya.bookstore.cart.api.CartSnapshot;
import com.honya.bookstore.catalog.api.CatalogStockApi;
import com.honya.bookstore.order.api.OrderApi;
import com.honya.bookstore.order.api.OrderItemRequest;
import com.honya.bookstore.order.api.OrderRequest;
import com.honya.bookstore.order.api.OrderResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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
        OrderApi orderApi = mock(OrderApi.class);
        CartApi cartApi = mock(CartApi.class);
        CatalogStockApi catalogStockApi = mock(CatalogStockApi.class);
        UUID userId = UUID.randomUUID();
        UUID firstBookId = UUID.randomUUID();
        UUID secondBookId = UUID.randomUUID();
        CheckoutRequestDTO request = new CheckoutRequestDTO();
        request.setFirstName("Ada");
        request.setLastName("Lovelace");
        request.setAddress("12 Example Street");
        request.setCity("London");

        when(cartApi.getCheckoutSnapshot(userId.toString())).thenReturn(new CartSnapshot(userId, List.of(
                new CartItemSnapshot(firstBookId, 2),
                new CartItemSnapshot(secondBookId, 1)
        )));
        when(catalogStockApi.getBookPrice(firstBookId)).thenReturn(100);
        when(catalogStockApi.getBookPrice(secondBookId)).thenReturn(250);
        OrderResponse response = new OrderResponse(UUID.randomUUID(), "Ada", "Lovelace", "12 Example Street", "London", null, null, null, 450, userId, List.of());
        when(orderApi.createOrder(org.mockito.ArgumentMatchers.eq(userId.toString()), any(OrderRequest.class))).thenReturn(response);

        OrderResponse createdOrder = new CheckoutService(orderApi, cartApi, catalogStockApi)
                .checkout(userId.toString(), request);

        ArgumentCaptor<OrderRequest> orderCaptor = ArgumentCaptor.forClass(OrderRequest.class);
        verify(orderApi).createOrder(org.mockito.ArgumentMatchers.eq(userId.toString()), orderCaptor.capture());
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
        verify(cartApi, never()).clearCart(any(UUID.class));
    }
}
