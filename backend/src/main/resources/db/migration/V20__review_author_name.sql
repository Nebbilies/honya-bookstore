ALTER TABLE review.reviews
    ADD COLUMN IF NOT EXISTS author_name VARCHAR(255);
