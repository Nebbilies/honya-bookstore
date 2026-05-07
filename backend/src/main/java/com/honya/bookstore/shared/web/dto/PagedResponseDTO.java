package com.honya.bookstore.shared.web.dto;

import java.util.List;

public record PagedResponseDTO<T>(
        List<T> data,
        PageMetaDTO meta
) {
}
