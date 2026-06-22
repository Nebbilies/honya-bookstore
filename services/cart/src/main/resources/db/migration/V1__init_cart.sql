CREATE SCHEMA IF NOT EXISTS cart;

CREATE TABLE cart.carts (
    id UUID PRIMARY KEY,
    user_id UUID,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE cart.cart_items (
    id UUID PRIMARY KEY,
    cart_id UUID REFERENCES cart.carts (id),
    catalog_item_id UUID,
    title VARCHAR(255),
    author VARCHAR(255),
    image_url VARCHAR(255),
    unit_price INTEGER,
    quantity INTEGER
);

CREATE INDEX idx_cart_items_catalog_item_id
    ON cart.cart_items (catalog_item_id);

CREATE TABLE cart.cart_processed_order_events (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL UNIQUE,
    processed_at TIMESTAMP WITH TIME ZONE
);
