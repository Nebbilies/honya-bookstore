package com.honya.bookstore.catalog.web.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class StockReservationRequestDTO {
    private UUID sagaId;
    private Integer quantity;
}
