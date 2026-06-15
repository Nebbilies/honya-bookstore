package com.honya.bookstore.shared.error;

import org.springframework.http.HttpStatus;

public class InvalidReviewException extends ApplicationException {

    public InvalidReviewException(String detail) {
        super(
                HttpStatus.BAD_REQUEST,
                "Invalid review",
                "INVALID_REVIEW",
                detail
        );
    }
}
