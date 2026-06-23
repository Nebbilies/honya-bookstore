package com.honya.bookstore.catalog.application;

import com.honya.bookstore.shared.integration.saga.RabbitSagaCommandConfig;
import com.honya.bookstore.shared.integration.saga.command.ReleaseStockCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class SagaCommandListener {

    private final BookService bookService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = RabbitSagaCommandConfig.CATALOG_QUEUE)
    public void onReleaseStock(String payload) {
        ReleaseStockCommand command = deserialize(payload);
        bookService.releaseStock(command.sagaId(), command.bookId(), command.quantity());
    }

    private ReleaseStockCommand deserialize(String payload) {
        try {
            return objectMapper.readValue(payload, ReleaseStockCommand.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to handle saga command", ex);
        }
    }
}
