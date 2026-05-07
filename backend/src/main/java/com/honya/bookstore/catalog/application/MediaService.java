package com.honya.bookstore.catalog.application;

import com.honya.bookstore.catalog.web.dto.request.CreateMediaRequestDTO;
import com.honya.bookstore.catalog.web.dto.response.MediaResponseDTO;
import com.honya.bookstore.catalog.web.dto.response.UploadImageURLResponseDTO;
import io.minio.errors.MinioException;

import java.util.List;

public interface MediaService {
    UploadImageURLResponseDTO generateUploadURL() throws MinioException;

    List<MediaResponseDTO> getMedia(int page, int limit);

    MediaResponseDTO createMedia(CreateMediaRequestDTO requestDTO);
}
