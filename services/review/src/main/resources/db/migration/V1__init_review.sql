CREATE SCHEMA IF NOT EXISTS review;

CREATE TABLE review.reviews (
    id UUID PRIMARY KEY,
    rating INTEGER,
    vote_count INTEGER,
    content TEXT,
    user_id UUID,
    author_name VARCHAR(255),
    book_id UUID,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE review.review_votes (
    id UUID PRIMARY KEY,
    is_up BOOLEAN,
    user_id UUID,
    review_id UUID,
    CONSTRAINT uk_review_votes_review_voter UNIQUE (review_id, user_id)
);

CREATE INDEX idx_reviews_book_id ON review.reviews (book_id);
CREATE INDEX idx_review_votes_review_id ON review.review_votes (review_id);
