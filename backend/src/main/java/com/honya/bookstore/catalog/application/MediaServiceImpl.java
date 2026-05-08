package com.honya.bookstore.catalog.application;

import com.honya.bookstore.catalog.domain.Media;
import com.honya.bookstore.catalog.infrastructure.persistence.MediaRepository;
import com.honya.bookstore.catalog.web.dto.request.CreateMediaRequestDTO;
import com.honya.bookstore.catalog.web.dto.response.MediaResponseDTO;
import com.honya.bookstore.catalog.web.dto.response.UploadImageURLResponseDTO;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.Http;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MediaServiceImpl implements MediaService {

    private final MinioClient presignMinioClient;
    private final MediaRepository mediaRepository;

    public MediaServiceImpl(
            @Qualifier("publicMinioClient") MinioClient presignMinioClient,
            MediaRepository mediaRepository
    ) {
        this.presignMinioClient = presignMinioClient;
        this.mediaRepository = mediaRepository;
    }

    @Value("${spring.minio.media-bucket-name}")
    private String bucketName;

    @Value("${spring.minio.public-url}")
    private String minioPublicUrl;

    @Override
    public UploadImageURLResponseDTO generateUploadURL() throws MinioException {
        final String mediaKey = "images/" + UUID.randomUUID();
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .expiry(60 * 60)
                .method(Http.Method.PUT)
                .bucket(bucketName)
                .object(mediaKey)
                .build();

        String url = presignMinioClient.getPresignedObjectUrl(args);
        return UploadImageURLResponseDTO.builder()
                .key(mediaKey)
                .url(url)
                .build();
    }

    @Override
    public List<MediaResponseDTO> getMedia(int page, int limit) {
        return mediaRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public MediaResponseDTO createMedia(CreateMediaRequestDTO requestDTO) {
        Media media = Media.builder()
                .altText(requestDTO.getAltText())
                .key(requestDTO.getKey())
                .order(requestDTO.getOrder())
                .url(buildPublicUrl(requestDTO.getKey()))
                .createdAt(OffsetDateTime.now())
                .deletedAt(OffsetDateTime.now())
                .build();

        Media saved = mediaRepository.save(media);
        return mapToResponse(saved);
    }

    private MediaResponseDTO mapToResponse(Media media) {
        return MediaResponseDTO.builder()
                .id(media.getId())
                .altText(media.getAltText())
                .order(media.getOrder())
                .url(media.getUrl())
                .createdAt(media.getCreatedAt())
                .deletedAt(media.getDeletedAt())
                .build();
    }

    private String buildPublicUrl(String key) {
        return minioPublicUrl + "/" + bucketName + "/" + key;
    }
}
