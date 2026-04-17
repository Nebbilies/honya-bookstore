package com.honya.bookstore.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "carts")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Cart {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private OffsetDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items;
}