package com.honya.bookstore.media.api;

import java.util.UUID;

public record MediaView(UUID id, String url, String altText, Integer order) {
}
