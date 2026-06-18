package com.honya.bookstore.media.web.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadImageURLResponseDTO {
    private String key;
    private String url;
}
