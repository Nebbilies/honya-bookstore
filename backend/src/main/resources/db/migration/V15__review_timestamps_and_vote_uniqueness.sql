ALTER TABLE review.reviews
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE;

UPDATE review.reviews
SET created_at = COALESCE(created_at, CURRENT_TIMESTAMP),
    updated_at = COALESCE(updated_at, CURRENT_TIMESTAMP),
    vote_count = COALESCE(vote_count, 0);

ALTER TABLE review.review_votes
    ADD CONSTRAINT uk_review_votes_review_voter UNIQUE (review_id, user_id);
