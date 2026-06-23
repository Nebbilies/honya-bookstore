CREATE TABLE checkout.checkout_outbox_messages (
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

CREATE INDEX idx_checkout_outbox_due
    ON checkout.checkout_outbox_messages (status, next_attempt_at, created_at);
