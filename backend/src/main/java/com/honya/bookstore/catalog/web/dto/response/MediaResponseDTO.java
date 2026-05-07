package com.honya.bookstore.catalog.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponseDTO {
    private UUID id;
    private String altText;
    private Integer order;
    private String url;
    private OffsetDateTime createdAt;
    private OffsetDateTime deletedAt;
}
