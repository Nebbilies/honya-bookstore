package com.honya.bookstore.catalog.application;

import com.honya.bookstore.catalog.infrastructure.persistence.BookMediaRepository;
import com.honya.bookstore.catalog.infrastructure.persistence.CatalogProcessedMediaEventRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MediaEventListenerTest {

    private final BookMediaRepository bookMediaRepository = mock(BookMediaRepository.class);
    private final CatalogProcessedMediaEventRepository processedRepository = mock(CatalogProcessedMediaEventRepository.class);
    private final MediaEventListener listener = new MediaEventListener(bookMediaRepository, processedRepository);

    @Test
    void removesBookMediaAndRecordsProcessedEvent() {
        UUID eventId = UUID.randomUUID();
        UUID mediaId = UUID.randomUUID();
        String payload = "{\"eventId\":\"" + eventId + "\",\"mediaId\":\"" + mediaId + "\"}";
        when(processedRepository.existsByEventId(eventId)).thenReturn(false);

        listener.handle(payload);

        verify(bookMediaRepository).deleteByMediaId(mediaId);
        verify(processedRepository).save(argThat(processed ->
                eventId.equals(processed.getEventId()) && mediaId.equals(processed.getMediaId())));
    }

    @Test
    void skipsAlreadyProcessedEvent() {
        UUID eventId = UUID.randomUUID();
        UUID mediaId = UUID.randomUUID();
        String payload = "{\"eventId\":\"" + eventId + "\",\"mediaId\":\"" + mediaId + "\"}";
        when(processedRepository.existsByEventId(eventId)).thenReturn(true);

        listener.handle(payload);

        verify(bookMediaRepository, never()).deleteByMediaId(any());
    }
}
