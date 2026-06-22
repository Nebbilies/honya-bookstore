package com.honya.bookstore.cart.web.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CartItemBookResponseDTO {
    private UUID id;
    private String title;
    private String author;
    private String imageUrl;
    private Integer price;
}
