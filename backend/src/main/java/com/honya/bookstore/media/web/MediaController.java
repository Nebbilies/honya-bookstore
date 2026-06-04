package com.honya.bookstore.media.web;

import com.honya.bookstore.media.application.MediaService;
import com.honya.bookstore.media.web.dto.request.CreateMediaRequestDTO;
import com.honya.bookstore.media.web.dto.response.MediaResponseDTO;
import com.honya.bookstore.media.web.dto.response.UploadImageURLResponseDTO;
import com.honya.bookstore.security.CustomerOnly;
import com.honya.bookstore.security.StaffOrAdmin;
import com.honya.bookstore.shared.PageMetaDTO;
import com.honya.bookstore.shared.PagedResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Media", description = "Endpoints for managing media files")
@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {
    private final MediaService mediaService;

    @Operation(summary = "Get media", description = "Retrieve media items")
    @CustomerOnly
    @GetMapping
    public ResponseEntity<PagedResponseDTO<MediaResponseDTO>> getMedia(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        List<MediaResponseDTO> media = mediaService.getMedia(page, limit);

        int safePage = Math.max(page, 1);
        int safeLimit = Math.max(limit, 1);
        int totalItems = media.size();
        int fromIndex = Math.min((safePage - 1) * safeLimit, totalItems);
        int toIndex = Math.min(fromIndex + safeLimit, totalItems);
        List<MediaResponseDTO> pageData = media.subList(fromIndex, toIndex);
        int totalPages = totalItems == 0 ? 0 : (int) Math.ceil((double) totalItems / safeLimit);

        PageMetaDTO meta = new PageMetaDTO(safePage, safeLimit, pageData.size(), totalItems, totalPages);

        return ResponseEntity.ok(new PagedResponseDTO<>(pageData, meta));
    }

    @Operation(summary = "Create media", description = "Create media metadata record")
    @StaffOrAdmin
    @PostMapping
    public ResponseEntity<MediaResponseDTO> createMedia(@RequestBody CreateMediaRequestDTO requestDTO) {
        MediaResponseDTO response = mediaService.createMedia(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Generate upload URL", description = "Generate pre-signed URL for uploading media files")
    @StaffOrAdmin
    @GetMapping("/images/upload-url")
    public ResponseEntity<UploadImageURLResponseDTO> generateUploadURL() {
        try {
            return ResponseEntity.ok(mediaService.generateUploadURL());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate upload URL", e);
        }
    }
}
