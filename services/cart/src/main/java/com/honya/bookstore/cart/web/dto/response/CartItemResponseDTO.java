package com.honya.bookstore.cart.web.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class CartItemResponseDTO {
    private UUID id;
    private CartItemBookResponseDTO book;
    private Integer quantity;
}