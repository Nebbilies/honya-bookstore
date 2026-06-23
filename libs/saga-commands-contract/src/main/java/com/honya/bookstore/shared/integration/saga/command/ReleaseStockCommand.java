package com.honya.bookstore.shared.integration.saga.command;

import java.util.UUID;

public record ReleaseStockCommand(UUID sagaId, UUID bookId, Integer quantity) {
}
