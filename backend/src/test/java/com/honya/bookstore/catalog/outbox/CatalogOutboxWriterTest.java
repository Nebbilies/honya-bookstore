package com.honya.bookstore.catalog.outbox;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CatalogOutboxWriterTest {

    @Test
    void enqueueStoresPendingCatalogEvent() {
        CatalogOutboxMessageRepository repository = mock(CatalogOutboxMessageRepository.class);
        CatalogOutboxEventSerializer serializer = mock(CatalogOutboxEventSerializer.class);
        CatalogOutboxWriter writer = new CatalogOutboxWriter(repository, serializer);

        UUID aggregateId = UUID.randomUUID();
        String payload = "{\"key\":\"value\"}";
        when(serializer.serialize(payload)).thenReturn(payload);

        writer.enqueue("PRODUCT_PRICE_CHANGED", aggregateId, payload);

        ArgumentCaptor<CatalogOutboxMessage> captor = ArgumentCaptor.forClass(CatalogOutboxMessage.class);
        verify(repository).save(captor.capture());

        CatalogOutboxMessage saved = captor.getValue();
        assertEquals("PRODUCT_PRICE_CHANGED", saved.getEventType());
        assertEquals(aggregateId, saved.getAggregateId());
        assertEquals(payload, saved.getPayload());
        assertEquals(CatalogOutboxStatus.PENDING, saved.getStatus());
        assertEquals(0, saved.getAttempts());
        assertNotNull(saved.getNextAttemptAt());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }
}
