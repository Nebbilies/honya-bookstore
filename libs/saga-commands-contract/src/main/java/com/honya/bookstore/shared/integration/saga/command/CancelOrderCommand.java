package com.honya.bookstore.shared.integration.saga.command;

import java.util.UUID;

public record CancelOrderCommand(UUID sagaId, UUID orderId) {
}
