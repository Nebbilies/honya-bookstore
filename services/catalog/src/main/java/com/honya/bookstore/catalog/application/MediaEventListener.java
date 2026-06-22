package com.honya.bookstore.catalog.application;

import tools.jackson.databind.ObjectMapper;
import com.honya.bookstore.catalog.infrastructure.persistence.BookMediaRepository;
import com.honya.bookstore.catalog.infrastructure.persistence.CatalogProcessedMediaEventRepository;
import com.honya.bookstore.shared.integration.media.RabbitMediaIntegrationConfig;
import com.honya.bookstore.shared.integration.media.event.MediaDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Component("catalogMediaEventListener")
@RequiredArgsConstructor
public class MediaEventListener {

    private final BookMediaRepository bookMediaRepository;
    private final CatalogProcessedMediaEventRepository processedMediaEventRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = RabbitMediaIntegrationConfig.CATALOG_QUEUE)
    @Transactional
    public void handle(String payload) {
        MediaDeletedEvent event = deserialize(payload);

        if (processedMediaEventRepository.insertIfAbsent(event.eventId(), event.mediaId(), OffsetDateTime.now()) == 0) {
            return;
        }

        bookMediaRepository.deleteByMediaId(event.mediaId());
    }

    private MediaDeletedEvent deserialize(String payload) {
        try {
            return objectMapper.readValue(payload, MediaDeletedEvent.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to handle media integration event", ex);
        }
    }
}
