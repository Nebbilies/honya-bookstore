CREATE SCHEMA IF NOT EXISTS media;

CREATE TABLE media.media (
    id UUID PRIMARY KEY,
    url VARCHAR(255),
    alt_text VARCHAR(255),
    key VARCHAR(255),
    display_order INTEGER,
    created_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE media.media_outbox_messages (
    id UUID PRIMARY KEY,
    event_type VARCHAR(255) NOT NULL,
    aggregate_id UUID NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(255) NOT NULL,
    attempts INTEGER NOT NULL,
    next_attempt_at TIMESTAMP WITH TIME ZONE NOT NULL,
    sent_at TIMESTAMP WITH TIME ZONE,
    last_error VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_media_outbox_due
    ON media.media_outbox_messages (status, next_attempt_at, created_at);
