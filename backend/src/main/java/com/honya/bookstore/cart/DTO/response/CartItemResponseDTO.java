package com.honya.bookstore.cart.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class CartItemResponseDTO {
    private UUID id;
    private UUID bookId;
    private Integer quantity;
}