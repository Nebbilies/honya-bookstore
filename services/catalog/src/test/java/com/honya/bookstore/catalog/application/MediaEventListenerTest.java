package com.honya.bookstore.catalog.application;

import com.honya.bookstore.catalog.infrastructure.persistence.BookMediaRepository;
import com.honya.bookstore.catalog.infrastructure.persistence.CatalogProcessedMediaEventRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MediaEventListenerTest {

    private final BookMediaRepository bookMediaRepository = mock(BookMediaRepository.class);
    private final CatalogProcessedMediaEventRepository processedRepository = mock(CatalogProcessedMediaEventRepository.class);
    private final MediaEventListener listener = new MediaEventListener(bookMediaRepository, processedRepository);

    @Test
    void removesBookMediaWhenEventIsNew() {
        UUID eventId = UUID.randomUUID();
        UUID mediaId = UUID.randomUUID();
        String payload = "{\"eventId\":\"" + eventId + "\",\"mediaId\":\"" + mediaId + "\"}";
        when(processedRepository.insertIfAbsent(eq(eventId), eq(mediaId), any())).thenReturn(1);

        listener.handle(payload);

        verify(bookMediaRepository).deleteByMediaId(mediaId);
    }

    @Test
    void skipsAlreadyProcessedEvent() {
        UUID eventId = UUID.randomUUID();
        UUID mediaId = UUID.randomUUID();
        String payload = "{\"eventId\":\"" + eventId + "\",\"mediaId\":\"" + mediaId + "\"}";
        when(processedRepository.insertIfAbsent(eq(eventId), eq(mediaId), any())).thenReturn(0);

        listener.handle(payload);

        verify(bookMediaRepository, never()).deleteByMediaId(any());
    }
}
