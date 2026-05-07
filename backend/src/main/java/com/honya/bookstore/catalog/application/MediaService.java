package com.honya.bookstore.catalog.application;

import com.honya.bookstore.catalog.web.dto.response.UploadImageURLResponseDTO;
import io.minio.errors.MinioException;

public interface MediaService {
    public UploadImageURLResponseDTO generateUploadURL() throws MinioException;
}
