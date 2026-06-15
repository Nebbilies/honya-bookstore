package com.honya.bookstore.order.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "order_items_books", schema = "\"order\"")
@Data @NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderItemBook {
    @Id
    private UUID id;
    private Integer price;
    private String author;
    private Integer rating;
    private String title;
}
