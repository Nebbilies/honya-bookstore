package com.honya.bookstore.checkout;

import com.honya.bookstore.cart.api.CartApi;
import com.honya.bookstore.catalog.api.CatalogStockApi;
import com.honya.bookstore.order.Order;
import com.honya.bookstore.order.OrderItem;
import com.honya.bookstore.order.api.OrderApi;
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

    public Order checkout(String userId, CheckoutRequestDTO request) {
        List<OrderItem> items = cartApi.getCheckoutSnapshot(userId).items().stream()
                .map(item -> {
                    Integer price = catalogStockApi.getBookPrice(item.bookId());
                    return OrderItem.builder()
                            .bookId(item.bookId())
                            .quantity(item.quantity())
                            .price(price)
                            .build();
                })
                .collect(Collectors.toList());

        Order order = Order.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .address(request.getAddress())
                .city(request.getCity())
                .items(items)
                .totalAmount(items.stream()
                        .mapToInt(item -> item.getPrice() * item.getQuantity())
                        .sum())
                .build();

        return orderApi.createOrder(userId, order);
    }
}
