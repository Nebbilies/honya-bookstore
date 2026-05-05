package com.honya.bookstore.cart.web.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class AddItemRequestDTO {
    private UUID bookId;
    private Integer quantity;
}