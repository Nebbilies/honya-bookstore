package com.honya.bookstore.checkout;

import com.honya.bookstore.cart.api.CartApi;
import com.honya.bookstore.cart.api.CartItemSnapshot;
import com.honya.bookstore.cart.api.CartSnapshot;
import com.honya.bookstore.catalog.api.CatalogStockApi;
import com.honya.bookstore.order.Order;
import com.honya.bookstore.order.api.OrderApi;
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
        when(orderApi.createOrder(org.mockito.ArgumentMatchers.eq(userId.toString()), any(Order.class))).thenAnswer(invocation -> invocation.getArgument(1));

        Order createdOrder = new CheckoutService(orderApi, cartApi, catalogStockApi)
                .checkout(userId.toString(), request);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderApi).createOrder(org.mockito.ArgumentMatchers.eq(userId.toString()), orderCaptor.capture());
        Order order = orderCaptor.getValue();
        assertSame(order, createdOrder);
        assertEquals("Ada", order.getFirstName());
        assertEquals("Lovelace", order.getLastName());
        assertEquals("12 Example Street", order.getAddress());
        assertEquals("London", order.getCity());
        assertEquals(450, order.getTotalAmount());
        assertEquals(2, order.getItems().size());
        assertEquals(firstBookId, order.getItems().get(0).getBookId());
        assertEquals(2, order.getItems().get(0).getQuantity());
        assertEquals(100, order.getItems().get(0).getPrice());
        assertEquals(secondBookId, order.getItems().get(1).getBookId());
        assertEquals(1, order.getItems().get(1).getQuantity());
        assertEquals(250, order.getItems().get(1).getPrice());
        verify(cartApi, never()).clearCart(any(UUID.class));
    }
}
