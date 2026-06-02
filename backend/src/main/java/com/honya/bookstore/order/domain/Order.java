package com.honya.bookstore.order.domain;

import com.honya.bookstore.order.domain.*; // Assuming you moved the enums into an 'enums' subfolder
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders", schema = "\"order\"")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String phone;
    private String email;
    private String paymentUrl;
    private String paymentTransactionNo;
    private OffsetDateTime paidAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private OrderProvider provider;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Boolean isPaid;
    private Integer totalAmount;

    @Column(name = "user_id")
    private UUID userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
}