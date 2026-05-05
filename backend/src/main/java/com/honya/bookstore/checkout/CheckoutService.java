package com.honya.bookstore.checkout;

import com.honya.bookstore.cart.api.CartApi;
import com.honya.bookstore.catalog.api.CatalogStockApi;
import com.honya.bookstore.order.api.OrderApi;
import com.honya.bookstore.order.api.OrderItemRequest;
import com.honya.bookstore.order.api.OrderRequest;
import com.honya.bookstore.order.api.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final OrderApi orderApi;
    private final CartApi cartApi;
    private final CatalogStockApi catalogStockApi;

    public OrderResponse checkout(String userId, CheckoutRequestDTO request) {
        List<OrderItemRequest> items = cartApi.getCheckoutSnapshot(userId).items().stream()
                .map(item -> {
                    Integer price = catalogStockApi.getBookPrice(item.bookId());
                    return new OrderItemRequest(item.bookId(), item.quantity(), price);
                })
                .collect(Collectors.toList());

        OrderRequest order = new OrderRequest(
                request.getFirstName(),
                request.getLastName(),
                request.getAddress(),
                request.getCity(),
                items,
                items.stream()
                        .mapToInt(item -> item.price() * item.quantity())
                        .sum()
        );

        return orderApi.createOrder(userId, order);
    }
}
