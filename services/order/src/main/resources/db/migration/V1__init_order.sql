CREATE SCHEMA IF NOT EXISTS "order";

CREATE TABLE "order".orders (
    id UUID PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    address VARCHAR(255),
    city VARCHAR(255),
    phone VARCHAR(255),
    email VARCHAR(255),
    provider VARCHAR(255),
    status VARCHAR(255),
    is_paid BOOLEAN,
    total_amount INTEGER,
    payment_url TEXT,
    payment_transaction_no VARCHAR(255),
    paid_at TIMESTAMP WITH TIME ZONE,
    user_id UUID,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE "order".order_items_books (
    id UUID PRIMARY KEY,
    title VARCHAR(255),
    author VARCHAR(255),
    price INTEGER,
    rating INTEGER
);

CREATE TABLE "order".order_items (
    id UUID PRIMARY KEY,
    order_id UUID REFERENCES "order".orders (id),
    book_id UUID REFERENCES "order".order_items_books (id),
    quantity INTEGER,
    price INTEGER
);

CREATE TABLE "order".order_outbox_messages (
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

CREATE INDEX idx_order_outbox_due
    ON "order".order_outbox_messages (status, next_attempt_at, created_at);
