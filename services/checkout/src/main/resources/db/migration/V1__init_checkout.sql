CREATE SCHEMA IF NOT EXISTS checkout;

CREATE TABLE checkout.saga_instance (
    id UUID PRIMARY KEY,
    user_id UUID,
    order_id UUID,
    provider VARCHAR(255),
    status VARCHAR(255) NOT NULL,
    lines TEXT,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    expires_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_saga_status_expires
    ON checkout.saga_instance (status, expires_at);
