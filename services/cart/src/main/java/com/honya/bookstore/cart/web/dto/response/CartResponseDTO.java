package com.honya.bookstore.cart.web.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CartResponseDTO {
    private UUID id;
    private UUID ownerId;
    private OffsetDateTime updatedAt;
    private List<CartItemResponseDTO> items;
}