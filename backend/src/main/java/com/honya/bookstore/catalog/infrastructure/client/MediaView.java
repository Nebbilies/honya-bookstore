package com.honya.bookstore.catalog.infrastructure.client;

import java.util.UUID;

public record MediaView(UUID id, String url, String altText, Integer order) {
}
