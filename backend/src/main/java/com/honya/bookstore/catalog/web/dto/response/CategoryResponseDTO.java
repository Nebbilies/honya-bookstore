package com.honya.bookstore.catalog.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {
    private UUID id;
    private String name;
    private String slug;
    private String description;
}