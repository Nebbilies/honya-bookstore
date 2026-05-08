package com.honya.bookstore.shared;

public record PageMetaDTO(
        int currentPage,
        int itemsPerPage,
        int pageItems,
        long totalItems,
        int totalPages
) {
}
