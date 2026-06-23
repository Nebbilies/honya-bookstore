package com.honya.bookstore.order.application;

import com.honya.bookstore.order.domain.Order;
import com.honya.bookstore.order.domain.OrderItem;
import com.honya.bookstore.order.domain.OrderItemBook;
import com.honya.bookstore.order.domain.OrderPlacedDomainEvent;
import com.honya.bookstore.order.domain.PaymentConfirmedDomainEvent;
import com.honya.bookstore.order.domain.PaymentFailedDomainEvent;
import com.honya.bookstore.order.domain.OrderProvider;
import com.honya.bookstore.order.domain.OrderStatus;
import com.honya.bookstore.order.infrastructure.persistence.OrderItemBookRepository;
import com.honya.bookstore.order.infrastructure.persistence.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderServiceImplTest {

    @Test
    void createOrderPlacesOrderAndPersists() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        OrderItemBookRepository orderItemBookRepository = mock(OrderItemBookRepository.class);
        when(orderItemBookRepository.save(any(OrderItemBook.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        Order orderDetails = Order.builder()
                .firstName("Ada")
                .lastName("Lovelace")
                .address("12 Example Street")
                .city("London")
                .items(List.of(OrderItem.builder()
                        .book(OrderItemBook.builder().id(bookId).build())
                        .quantity(2)
                        .price(100)
                        .build()))
                .build();

        Order createdOrder = new OrderServiceImpl(orderRepository, orderItemBookRepository)
                .createOrder(userId.toString(), orderDetails);

        // Aggregate placed: identity assigned, user set, items linked back, defaults applied.
        assertNotNull(createdOrder.getId());
        assertEquals(userId, createdOrder.getUserId());
        assertSame(createdOrder, createdOrder.getItems().get(0).getOrder());
        assertEquals(OrderStatus.PENDING, createdOrder.getStatus());
        assertFalse(createdOrder.getIsPaid());
        assertNotNull(createdOrder.getCreatedAt());
        assertNotNull(createdOrder.getUpdatedAt());

        // Book snapshot is upserted before the order so the order_items FK is satisfied.
        verify(orderItemBookRepository).save(argThat(book -> bookId.equals(book.getId())));
    }

    @SuppressWarnings("unchecked")
    private static List<Object> domainEvents(Order order) {
        return (List<Object>) ReflectionTestUtils.getField(order, "domainEvents");
    }

    private static Order orderDetailsWithProvider(OrderProvider provider, UUID bookId) {
        return Order.builder()
                .firstName("Ada")
                .provider(provider)
                .items(List.of(OrderItem.builder()
                        .book(OrderItemBook.builder().id(bookId).build())
                        .quantity(2)
                        .price(100)
                        .build()))
                .build();
    }

    @Test
    void createOrderForCodConfirmsAndRegistersOrderPlaced() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        OrderItemBookRepository orderItemBookRepository = mock(OrderItemBookRepository.class);
        when(orderItemBookRepository.save(any(OrderItemBook.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order created = new OrderServiceImpl(orderRepository, orderItemBookRepository)
                .createOrder(UUID.randomUUID().toString(), orderDetailsWithProvider(OrderProvider.COD, UUID.randomUUID()));

        assertEquals(1, domainEvents(created).size());
    }

    @Test
    void createOrderForVnpayDoesNotRegisterOrderPlaced() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        OrderItemBookRepository orderItemBookRepository = mock(OrderItemBookRepository.class);
        when(orderItemBookRepository.save(any(OrderItemBook.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order created = new OrderServiceImpl(orderRepository, orderItemBookRepository)
                .createOrder(UUID.randomUUID().toString(), orderDetailsWithProvider(OrderProvider.VNPAY, UUID.randomUUID()));

        assertEquals(0, domainEvents(created).size());
    }

    @Test
    void updatePaymentStatusPaidConfirmsAndRegistersOrderPlaced() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        OrderItemBookRepository orderItemBookRepository = mock(OrderItemBookRepository.class);

        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        Order existing = Order.builder()
                .id(orderId)
                .userId(userId)
                .provider(OrderProvider.VNPAY)
                .status(OrderStatus.PENDING)
                .isPaid(false)
                .items(List.of(OrderItem.builder()
                        .book(OrderItemBook.builder().id(bookId).build())
                        .quantity(2)
                        .price(100)
                        .build()))
                .build();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existing));

        Order result = new OrderServiceImpl(orderRepository, orderItemBookRepository)
                .updatePaymentStatus(orderId, true, "txn-1", "PROCESSING");

        List<Object> events = domainEvents(result);
        OrderPlacedDomainEvent placed = events.stream()
                .filter(e -> e instanceof OrderPlacedDomainEvent)
                .map(e -> (OrderPlacedDomainEvent) e)
                .findFirst().orElseThrow();
        assertEquals(orderId, placed.orderId());
        assertEquals(userId, placed.userId());
        assertEquals(bookId, placed.lines().get(0).bookId());

        PaymentConfirmedDomainEvent confirmed = events.stream()
                .filter(e -> e instanceof PaymentConfirmedDomainEvent)
                .map(e -> (PaymentConfirmedDomainEvent) e)
                .findFirst().orElseThrow();
        assertEquals(orderId, confirmed.orderId());
        assertEquals("txn-1", confirmed.transactionNo());
    }

    @Test
    void recordPaymentFailureRegistersPaymentFailedEvent() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        OrderItemBookRepository orderItemBookRepository = mock(OrderItemBookRepository.class);

        UUID orderId = UUID.randomUUID();
        Order existing = Order.builder()
                .id(orderId)
                .userId(UUID.randomUUID())
                .provider(OrderProvider.VNPAY)
                .status(OrderStatus.PENDING)
                .isPaid(false)
                .items(List.of())
                .build();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existing));

        Order result = new OrderServiceImpl(orderRepository, orderItemBookRepository)
                .recordPaymentFailure(orderId, "VNPAY_24");

        PaymentFailedDomainEvent failed = domainEvents(result).stream()
                .filter(e -> e instanceof PaymentFailedDomainEvent)
                .map(e -> (PaymentFailedDomainEvent) e)
                .findFirst().orElseThrow();
        assertEquals(orderId, failed.orderId());
        assertEquals("VNPAY_24", failed.reason());
    }
}
