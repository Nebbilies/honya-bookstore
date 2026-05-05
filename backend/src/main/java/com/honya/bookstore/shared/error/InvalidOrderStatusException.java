package com.honya.bookstore.shared.error;

import org.springframework.http.HttpStatus;

public class InvalidOrderStatusException extends ApplicationException {

    public InvalidOrderStatusException(String status) {
        super(
                HttpStatus.BAD_REQUEST,
                "Invalid order status",
                "INVALID_ORDER_STATUS",
                "Invalid order status: " + status
        );
    }
}
