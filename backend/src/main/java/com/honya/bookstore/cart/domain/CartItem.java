package com.honya.bookstore.cart.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "cart_items", schema = "cart")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column(name = "book_id")
    private UUID bookId;

    private Integer quantity;
}