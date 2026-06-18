package com.honya.bookstore.shared;

import java.util.List;

public record PagedResponseDTO<T>(
        List<T> data,
        PageMetaDTO meta
) {
}
