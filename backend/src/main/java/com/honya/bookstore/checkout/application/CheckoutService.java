package com.honya.bookstore.checkout.application;

import com.honya.bookstore.shared.integration.cart.CartClient;
import com.honya.bookstore.shared.integration.catalog.CatalogClient;
import com.honya.bookstore.shared.integration.order.OrderClient;
import com.honya.bookstore.shared.integration.order.OrderItemRequest;
import com.honya.bookstore.shared.integration.order.OrderRequest;
import com.honya.bookstore.shared.integration.order.OrderResponse;
import com.honya.bookstore.checkout.web.dto.CheckoutRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final OrderClient orderClient;
    private final CartClient cartClient;
    private final CatalogClient catalogClient;

    public OrderResponse checkout(String userId, CheckoutRequestDTO request) {
        List<OrderItemRequest> items = cartClient.getCheckoutSnapshot(userId).items().stream()
                .map(item -> {
                    Integer price = catalogClient.getBook(item.bookId()).price();
                    return new OrderItemRequest(item.bookId(), item.quantity(), price);
                })
                .collect(Collectors.toList());

        OrderRequest order = new OrderRequest(
                request.getFirstName(),
                request.getLastName(),
                request.getAddress(),
                request.getCity(),
                null,
                null,
                null,
                null,
                items,
                items.stream()
                        .mapToInt(item -> item.price() * item.quantity())
                        .sum()
        );

        return orderClient.createOrder(userId, order);
    }
}
