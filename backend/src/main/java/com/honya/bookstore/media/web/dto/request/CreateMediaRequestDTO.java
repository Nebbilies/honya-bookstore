package com.honya.bookstore.media.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateMediaRequestDTO {
    private String altText;
    private String key;
    private Integer order;
}
