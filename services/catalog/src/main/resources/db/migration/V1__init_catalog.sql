CREATE SCHEMA IF NOT EXISTS catalog;

CREATE TABLE catalog.categories (
    id UUID PRIMARY KEY,
    slug VARCHAR(255),
    name VARCHAR(255),
    description VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE catalog.books (
    id UUID PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    author VARCHAR(255),
    price INTEGER,
    pages_count INTEGER,
    year_published INTEGER,
    publisher VARCHAR(255),
    weight REAL,
    stock_quantity INTEGER,
    purchase_count INTEGER,
    rating REAL,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE catalog.book_categories (
    book_id UUID NOT NULL REFERENCES catalog.books (id),
    category_id UUID NOT NULL REFERENCES catalog.categories (id)
);

CREATE TABLE catalog.book_media (
    id UUID PRIMARY KEY,
    book_id UUID REFERENCES catalog.books (id),
    media_id UUID,
    media_url VARCHAR(255),
    media_alt_text VARCHAR(255),
    is_cover BOOLEAN,
    media_order INTEGER
);

CREATE TABLE catalog.catalog_outbox_messages (
    id UUID PRIMARY KEY,
    event_type VARCHAR(255),
    aggregate_id UUID,
    payload TEXT,
    status VARCHAR(255),
    attempts INTEGER NOT NULL,
    next_attempt_at TIMESTAMP WITH TIME ZONE,
    sent_at TIMESTAMP WITH TIME ZONE,
    last_error VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_catalog_outbox_due
    ON catalog.catalog_outbox_messages (status, next_attempt_at, created_at);

CREATE TABLE catalog.catalog_processed_media_events (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL UNIQUE,
    media_id UUID NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE catalog.catalog_processed_order_events (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL UNIQUE,
    processed_at TIMESTAMP WITH TIME ZONE
);
