package com.honya.bookstore.shared.error;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class InsufficientStockException extends ApplicationException {

    public InsufficientStockException(UUID bookId, String title, int requested, int available) {
        super(
                HttpStatus.CONFLICT,
                "Insufficient stock",
                "INSUFFICIENT_STOCK",
                "Insufficient stock for book " + title + ": requested " + requested + ", available " + available
        );
    }
}
