package com.honya.bookstore.catalog.application;

import com.honya.bookstore.catalog.web.dto.response.UploadImageURLResponseDTO;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.Http;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MinioClient minioClient;
    @Value("${spring.minio.media-bucket-name}")
    private String bucketName;

    public UploadImageURLResponseDTO generateUploadURL() throws MinioException {
        final String mediaKey = "images/" + java.util.UUID.randomUUID().toString();
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .expiry(60 * 60) // URL valid for 1 hour
                .method(Http.Method.PUT)
                .bucket(bucketName)
                .object(mediaKey)
                .build();

        String url = minioClient.getPresignedObjectUrl(args);
        return UploadImageURLResponseDTO.builder()
                .key(mediaKey)
                .url(url)
                .build();
    }
}
