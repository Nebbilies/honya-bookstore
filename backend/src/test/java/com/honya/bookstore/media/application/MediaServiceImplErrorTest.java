package com.honya.bookstore.media.application;

import com.honya.bookstore.media.infrastructure.persistence.MediaRepository;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MediaServiceImplErrorTest {

    @Test
    void generateUploadURLUsesPublicMinioClientEndpoint() throws Exception {
        MinioClient publicMinioClient = mock(MinioClient.class);
        MediaRepository mediaRepository = mock(MediaRepository.class);

        when(publicMinioClient.getPresignedObjectUrl(any()))
                .thenReturn("http://localhost:9000/media/images/demo?X-Amz-Signature=abc");

        MediaServiceImpl service = new MediaServiceImpl(publicMinioClient, mediaRepository);
        setField(service, "bucketName", "media");
        setField(service, "minioPublicUrl", "http://localhost:9000");

        String url = service.generateUploadURL().getUrl();

        assertTrue(url.startsWith("http://localhost:9000"));
    }

    private void setField(Object target, String name, String value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
