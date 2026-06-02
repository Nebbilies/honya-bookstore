package com.honya.bookstore.cart.application;

import tools.jackson.databind.ObjectMapper;
import com.honya.bookstore.shared.integration.catalog.event.ProductDetailsChangedEvent;
import com.honya.bookstore.shared.integration.catalog.event.ProductPriceChangedEvent;
import com.honya.bookstore.shared.integration.catalog.event.ProductRemovedEvent;
import com.honya.bookstore.shared.integration.catalog.RabbitCatalogIntegrationConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class CatalogProductEventListener {

    private final CartService cartService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = RabbitCatalogIntegrationConfig.CART_QUEUE)
    public void handle(Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        String payload = new String(message.getBody());

        try {
            switch (routingKey) {
                case RabbitCatalogIntegrationConfig.PRICE_CHANGED ->
                        handlePriceChanged(objectMapper.readValue(payload, ProductPriceChangedEvent.class));
                case RabbitCatalogIntegrationConfig.DETAILS_CHANGED ->
                        handleDetailsChanged(objectMapper.readValue(payload, ProductDetailsChangedEvent.class));
                case RabbitCatalogIntegrationConfig.REMOVED ->
                        handleProductRemoved(objectMapper.readValue(payload, ProductRemovedEvent.class));
                default -> log.warn("Skip unknown catalog routing key: {}", routingKey);
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to handle catalog integration event", ex);
        }
    }

    void handlePriceChanged(ProductPriceChangedEvent event) {
        cartService.updateSnapshotForCatalogItem(event.catalogItemId(), null, null, null, event.price());
    }

    void handleDetailsChanged(ProductDetailsChangedEvent event) {
        cartService.updateSnapshotForCatalogItem(
                event.catalogItemId(),
                event.title(),
                event.author(),
                event.imageUrl(),
                event.price()
        );
    }

    void handleProductRemoved(ProductRemovedEvent event) {
        cartService.removeItemsByCatalogItemId(event.catalogItemId());
    }
}
