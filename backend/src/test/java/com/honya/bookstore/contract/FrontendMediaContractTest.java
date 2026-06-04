package com.honya.bookstore.contract;

import com.honya.bookstore.media.application.MediaService;
import com.honya.bookstore.media.web.MediaController;
import com.honya.bookstore.media.web.dto.request.CreateMediaRequestDTO;
import com.honya.bookstore.media.web.dto.response.MediaResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FrontendMediaContractTest {

    private MockMvc mockMvc;
    private MediaService mediaService;

    @BeforeEach
    void setUp() {
        mediaService = mock(MediaService.class);
        MediaController mediaController = new MediaController(mediaService);

        mockMvc = MockMvcBuilders.standaloneSetup(mediaController).build();
    }

    @Test
    void getMedia_returns_data_meta_and_media_fields() throws Exception {
        when(mediaService.getMedia(1, 10)).thenReturn(List.of(sampleMedia()));

        mockMvc.perform(get("/api/media?page=1&limit=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.meta.totalItems").exists())
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].altText").exists())
                .andExpect(jsonPath("$.data[0].order").isNumber())
                .andExpect(jsonPath("$.data[0].url").isString())
                .andExpect(jsonPath("$.data[0].createdAt").exists())
                .andExpect(jsonPath("$.data[0].deletedAt").exists());
    }

    @Test
    void createMedia_returns_frontend_media_shape() throws Exception {
        when(mediaService.createMedia(any(CreateMediaRequestDTO.class))).thenReturn(sampleMedia());

        mockMvc.perform(post("/api/media")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"altText":"Cover image","key":"books/cover.jpg","order":1}
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.altText").value("Cover image"))
                .andExpect(jsonPath("$.order").value(1))
                .andExpect(jsonPath("$.url").isString())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.deletedAt").exists());
    }

    private MediaResponseDTO sampleMedia() {
        return MediaResponseDTO.builder()
                .id(UUID.randomUUID())
                .altText("Cover image")
                .order(1)
                .url("http://localhost:9000/media/books/cover.jpg")
                .createdAt(OffsetDateTime.parse("2026-05-04T10:15:30Z"))
                .deletedAt(OffsetDateTime.parse("2026-05-04T10:15:30Z"))
                .build();
    }
}
