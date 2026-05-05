# Phase 8 Readiness Pack

## Standardized package map

| Module | API | Web | Application | Domain | Infrastructure |
| --- | --- | --- | --- | --- | --- |
| `catalog` | `catalog.api` | `catalog.web`, `catalog.web.dto` | `catalog.application` | `catalog.domain` | `catalog.infrastructure.persistence` |
| `cart` | `cart.api` | `cart.web`, `cart.web.dto` | `cart.application` | `cart.domain` | `cart.infrastructure.persistence` |
| `order` | `order.api`, `order.api.event` | `order.web`, `order.web.dto` | `order.application` | `order.domain` | `order.infrastructure.persistence`, `order.outbox` |
| `review` | none | none | none | `review.domain` | `review.infrastructure.persistence` |
| `discount` | none | none | none | `discount.domain` | `discount.infrastructure.persistence` |
| `ticket` | none | none | `ticket.application` | `ticket.domain` | `ticket.infrastructure.persistence` |
| `article` | none | none | none | `article.domain` | `article.infrastructure.persistence` |
| `user` | none | none | `user.application` | `user.domain` | `user.infrastructure.persistence` |
| `checkout` | none | none | `checkout.CheckoutService` | none | none |
| `outbox` | `outbox.core` | none | none | none | none |
| `shared` | `shared.error` | `shared.web` | none | none | none |

## Extraction readiness checklist

### Catalog
- Public API surface: `catalog.api.CatalogSeedApi`, `catalog.api.CatalogSeedApiAdapter`.
- Owned schema/tables: `catalog.books`, `catalog.categories`, `catalog.book_categories`, `catalog.media`, `catalog.book_media`, `catalog.catalog_processed_order_events`.
- Inbound HTTP endpoints: `/api/books`, `/api/categories`.
- Outbound module dependencies: none for core catalog behavior; subscribes to order API events.
- Published/subscribed events: subscribes to `order.api.event.OrderPlacedEvent`.
- Background jobs or schedulers: none.
- Migration risks: stock updates and idempotency table must move with catalog if extracted.

### Cart
- Public API surface: `cart.api.CartApi`, `cart.api.CartApiAdapter`.
- Owned schema/tables: `cart.carts`, `cart.cart_items`, `cart.cart_processed_order_events`.
- Inbound HTTP endpoints: `/api/cart`.
- Outbound module dependencies: none for core cart behavior; subscribes to order API events.
- Published/subscribed events: subscribes to `order.api.event.OrderPlacedEvent`.
- Background jobs or schedulers: none.
- Migration risks: cart cleanup idempotency table must move with cart if extracted.

### Order
- Public API surface: `order.api.OrderApi`, `order.api.OrderRequest`, `order.api.OrderItemRequest`, `order.api.OrderResponse`, `order.api.OrderItemResponse`, `order.api.event.OrderPlacedEvent`, `order.api.event.OrderItemEventDTO`.
- Owned schema/tables: `order.orders`, `order.order_items`, `order.order_outbox_messages`.
- Inbound HTTP endpoints: `/api/orders`, `/api/orders/checkout`.
- Outbound module dependencies: `catalog.api`, `cart.api`, `outbox.core`.
- Published/subscribed events: publishes `OrderPlacedEvent` through order outbox relay.
- Background jobs or schedulers: outbox relay polling for pending/failed order messages.
- Migration risks: order extraction needs transactional outbox ownership and API replacements for catalog/cart calls.

### Review
- Public API surface: none.
- Owned schema/tables: `review.reviews`, `review.review_votes`.
- Inbound HTTP endpoints: none.
- Outbound module dependencies: none.
- Published/subscribed events: none.
- Background jobs or schedulers: none.
- Migration risks: future HTTP/API surface should stay out of `review.domain` and repository access should stay module-local.

### Discount
- Public API surface: none.
- Owned schema/tables: `discount.discounts`, `discount.discount_condition_value`.
- Inbound HTTP endpoints: none.
- Outbound module dependencies: none.
- Published/subscribed events: none.
- Background jobs or schedulers: none.
- Migration risks: discount rules live in `discount.domain`; future consumers should use an `api` facade instead of domain/repository access.

### Ticket
- Public API surface: none.
- Owned schema/tables: `ticket.tickets`.
- Inbound HTTP endpoints: none.
- Outbound module dependencies: none.
- Published/subscribed events: none.
- Background jobs or schedulers: none.
- Migration risks: `ticket.application.TicketService` is an interface only; add implementation behind application boundary before exposing endpoints.

### Article
- Public API surface: none.
- Owned schema/tables: `article.articles`, `article.article_tags`.
- Inbound HTTP endpoints: none.
- Outbound module dependencies: none.
- Published/subscribed events: none.
- Background jobs or schedulers: none.
- Migration risks: article media linkage is ID-only; extracted service must preserve media ownership decision.

### User
- Public API surface: none.
- Owned schema/tables: `user.users`.
- Inbound HTTP endpoints: none.
- Outbound module dependencies: Keycloak outside app boundary.
- Published/subscribed events: none.
- Background jobs or schedulers: none.
- Migration risks: local `user.users` mirrors Keycloak IDs; extraction must preserve identity-provider contract.

## Dependency graph evidence

```text
checkout -> catalog.api
checkout -> cart.api
checkout -> order.api
order -> catalog.api
order -> cart.api
order -> outbox.core
catalog -> order.api.event
cart -> order.api.event
review -> none
discount -> none
ticket -> none
article -> none
user -> none
shared -> none
```

## Outbox flow proof

```text
OrderServiceImpl.createOrder
  -> persists order + order items
  -> OrderOutboxWriter.enqueue(OrderPlacedEvent)
  -> order.order_outbox_messages PENDING row
  -> OrderOutboxRelay publishes event
  -> catalog/cart listeners consume event idempotently
  -> relay marks message SENT or FAILED with backoff
```

## Schema ownership map

| Schema | Owner module |
| --- | --- |
| `catalog` | catalog |
| `cart` | cart |
| `order` | order |
| `review` | review |
| `discount` | discount |
| `ticket` | ticket |
| `article` | article |
| `user` | user |
| `shared` | shared/framework integration |
| `public.event_publication` | Spring Modulith event publication registry |

## Verification commands

```powershell
& "D:/Projects/honya-bookstore/backend/mvnw.cmd" -f "D:/Projects/honya-bookstore/backend/pom.xml" "-Dtest=com.honya.bookstore.contract.PackageNormalizationTest" test
& "D:/Projects/honya-bookstore/backend/mvnw.cmd" -f "D:/Projects/honya-bookstore/backend/pom.xml" "-Dtest=com.honya.bookstore.architecture.ModuleBoundaryEnforcementTest" test
& "D:/Projects/honya-bookstore/backend/mvnw.cmd" -f "D:/Projects/honya-bookstore/backend/pom.xml" "-Duser.timezone=UTC" test
```
