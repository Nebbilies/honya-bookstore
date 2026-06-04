CREATE SCHEMA IF NOT EXISTS media;
ALTER TABLE catalog.media SET SCHEMA media;

ALTER TABLE catalog.book_media
    ADD COLUMN media_url VARCHAR(255),
    ADD COLUMN media_alt_text VARCHAR(255);

UPDATE catalog.book_media bm SET
    media_url      = m.url,
    media_alt_text = m.alt_text,
    media_order    = m.display_order
FROM media.media m
WHERE bm.media_id = m.id;

ALTER TABLE catalog.book_media DROP CONSTRAINT fk_book_media_media;
