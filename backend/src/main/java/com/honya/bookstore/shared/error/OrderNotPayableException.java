package com.honya.bookstore.shared.error;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class OrderNotPayableException extends ApplicationException {

    public OrderNotPayableException(UUID orderId) {
        super(
                HttpStatus.BAD_REQUEST,
                "Order not payable",
                "ORDER_NOT_PAYABLE",
                "Order is not awaiting an online payment: " + orderId
        );
    }
}
