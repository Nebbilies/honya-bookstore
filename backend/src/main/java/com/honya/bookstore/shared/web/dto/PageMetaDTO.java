package com.honya.bookstore.shared.web.dto;

public record PageMetaDTO(
        int currentPage,
        int itemsPerPage,
        int pageItems,
        long totalItems,
        int totalPages
) {
}
