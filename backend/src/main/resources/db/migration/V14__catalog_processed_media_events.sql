CREATE TABLE catalog.catalog_processed_media_events (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL,
    media_id UUID NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uk_catalog_processed_media_events_event_id UNIQUE (event_id)
);
