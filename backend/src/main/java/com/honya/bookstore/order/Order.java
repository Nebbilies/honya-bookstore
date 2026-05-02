package com.honya.bookstore.domain.entity;

import com.honya.bookstore.domain.enums.*;
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User placedBy;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
}