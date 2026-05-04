package com.honya.bookstore.order.outbox;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.honya.bookstore.order.api.event.OrderItemEventDTO;
import com.honya.bookstore.order.api.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class OrderOutboxEventSerializer {

    private final ObjectMapper objectMapper;

    String serialize(OrderPlacedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to serialize OrderPlacedEvent", ex);
        }
    }

    OrderPlacedEvent deserialize(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            UUID orderId = UUID.fromString(root.get("orderId").asText());
            UUID userId = UUID.fromString(root.get("userId").asText());
            List<OrderItemEventDTO> items = new ArrayList<>();
            for (JsonNode item : root.path("items")) {
                items.add(new OrderItemEventDTO(
                        UUID.fromString(item.get("bookId").asText()),
                        item.get("quantity").asInt()
                ));
            }
            return new OrderPlacedEvent(orderId, userId, items);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to deserialize OrderPlacedEvent", ex);
        }
    }
}
