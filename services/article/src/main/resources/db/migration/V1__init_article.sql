CREATE SCHEMA IF NOT EXISTS article;

CREATE TABLE article.articles (
    id UUID PRIMARY KEY,
    slug VARCHAR(255),
    title VARCHAR(255),
    content TEXT,
    author_id UUID,
    media_id UUID,
    media_url VARCHAR(255),
    status VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE article.article_tags (
    article_id UUID NOT NULL REFERENCES article.articles (id),
    tags VARCHAR(255)
);

CREATE INDEX idx_article_tags_article_id ON article.article_tags (article_id);
CREATE INDEX idx_articles_slug ON article.articles (slug);
CREATE INDEX idx_articles_status ON article.articles (status);
