package com.honya.bookstore.media.application;

import com.honya.bookstore.media.web.dto.request.CreateMediaRequestDTO;
import com.honya.bookstore.media.web.dto.response.MediaResponseDTO;
import com.honya.bookstore.media.web.dto.response.UploadImageURLResponseDTO;
import io.minio.errors.MinioException;

import java.util.List;

public interface MediaService {
    UploadImageURLResponseDTO generateUploadURL() throws MinioException;

    List<MediaResponseDTO> getMedia(int page, int limit);

    MediaResponseDTO createMedia(CreateMediaRequestDTO requestDTO);
}
