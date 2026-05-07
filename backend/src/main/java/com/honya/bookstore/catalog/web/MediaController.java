package com.honya.bookstore.catalog.web;

import com.honya.bookstore.catalog.application.MediaService;
import com.honya.bookstore.catalog.web.dto.response.UploadImageURLResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Media", description = "Endpoints for managing media files in the catalog")
@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {
    private final MediaService mediaService;

    @Operation(summary = "Generate upload URL", description = "Generate pre-signed URL for uploading media files")
    @GetMapping("/images/upload-url")
    public ResponseEntity<UploadImageURLResponseDTO> generateUploadURL() {
        try {
            return ResponseEntity.ok(mediaService.generateUploadURL());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate upload URL", e);
        }
    }
}
