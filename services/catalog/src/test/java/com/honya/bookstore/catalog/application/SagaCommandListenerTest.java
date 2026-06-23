package com.honya.bookstore.catalog.application;

import com.honya.bookstore.shared.integration.saga.command.ReleaseStockCommand;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SagaCommandListenerTest {

    @Test
    void releaseCommandReleasesStock() {
        BookService bookService = mock(BookService.class);
        ObjectMapper mapper = new ObjectMapper();
        UUID sagaId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        String payload = mapper.writeValueAsString(new ReleaseStockCommand(sagaId, bookId, 3));

        new SagaCommandListener(bookService).onReleaseStock(payload);

        verify(bookService).releaseStock(sagaId, bookId, 3);
    }
}
