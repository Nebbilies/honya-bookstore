package com.honya.bookstore.order;

import com.honya.bookstore.order.enums.*; // Assuming you moved the enums into an 'enums' subfolder
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String firstName;
    private String lastName;
    private String address;
    private String city;

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