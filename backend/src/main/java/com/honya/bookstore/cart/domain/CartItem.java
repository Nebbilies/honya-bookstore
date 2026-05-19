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

    @Column(name = "catalog_item_id")
    private UUID catalogItemId;

    private String title;
    private String author;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "unit_price")
    private Integer unitPrice;

    private Integer quantity;
}