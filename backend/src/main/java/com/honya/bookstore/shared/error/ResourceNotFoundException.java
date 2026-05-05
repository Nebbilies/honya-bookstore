package com.honya.bookstore.shared.error;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ResourceNotFoundException extends ApplicationException {

    public ResourceNotFoundException(String resourceName, UUID id) {
        super(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                "RESOURCE_NOT_FOUND",
                resourceName + " not found with ID: " + id
        );
    }
}
