package com.honya.bookstore.shared.error;

import org.springframework.http.HttpStatus;

public abstract class ApplicationException extends RuntimeException {

    private final HttpStatus status;
    private final String title;
    private final String code;

    protected ApplicationException(HttpStatus status, String title, String code, String detail) {
        super(detail);
        this.status = status;
        this.title = title;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getCode() {
        return code;
    }
}
