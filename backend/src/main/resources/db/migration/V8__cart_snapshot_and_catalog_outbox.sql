ALTER TABLE cart.cart_items
    ADD COLUMN IF NOT EXISTS catalog_item_id UUID,
    ADD COLUMN IF NOT EXISTS title VARCHAR(255),
    ADD COLUMN IF NOT EXISTS author VARCHAR(255),
    ADD COLUMN IF NOT EXISTS image_url VARCHAR(500),
    ADD COLUMN IF NOT EXISTS unit_price INTEGER;

UPDATE cart.cart_items
SET catalog_item_id = book_id
WHERE catalog_item_id IS NULL;

UPDATE cart.cart_items item
SET title = book.title,
    author = COALESCE(book.author, 'Unknown'),
    unit_price = COALESCE(book.price, 0),
    image_url = COALESCE(cover.url, '/images/fallbackBookImage.png')
FROM catalog.books book
LEFT JOIN LATERAL (
    SELECT media.url
    FROM catalog.book_media book_media
    JOIN catalog.media media ON media.id = book_media.media_id
    WHERE book_media.book_id = book.id
      AND book_media.is_cover = TRUE
    ORDER BY book_media.media_order NULLS LAST, book_media.id
    LIMIT 1
) cover ON TRUE
WHERE item.book_id = book.id
  AND (item.title IS NULL OR item.author IS NULL OR item.unit_price IS NULL OR item.image_url IS NULL);

UPDATE cart.cart_items
SET title = COALESCE(title, 'Unavailable product'),
    author = COALESCE(author, 'Unknown'),
    unit_price = COALESCE(unit_price, 0),
    image_url = COALESCE(image_url, '/images/fallbackBookImage.png')
WHERE title IS NULL OR author IS NULL OR unit_price IS NULL OR image_url IS NULL;

ALTER TABLE cart.cart_items
    ALTER COLUMN catalog_item_id SET NOT NULL,
    ALTER COLUMN title SET NOT NULL,
    ALTER COLUMN author SET NOT NULL,
    ALTER COLUMN image_url SET NOT NULL,
    ALTER COLUMN unit_price SET NOT NULL;

CREATE INDEX IF NOT EXISTS idx_cart_items_catalog_item_id
    ON cart.cart_items (catalog_item_id);

CREATE TABLE IF NOT EXISTS catalog.catalog_outbox_messages (
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

CREATE INDEX IF NOT EXISTS idx_catalog_outbox_due
    ON catalog.catalog_outbox_messages (status, next_attempt_at, created_at);
