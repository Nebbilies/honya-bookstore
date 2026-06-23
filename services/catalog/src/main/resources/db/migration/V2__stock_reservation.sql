CREATE TABLE catalog.catalog_stock_reservations (
    id UUID PRIMARY KEY,
    saga_id UUID NOT NULL,
    book_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE UNIQUE INDEX uk_stock_reservation_saga_book
    ON catalog.catalog_stock_reservations (saga_id, book_id);
