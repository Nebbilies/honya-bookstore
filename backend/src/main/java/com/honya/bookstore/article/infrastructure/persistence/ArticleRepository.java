package com.honya.bookstore.article.infrastructure.persistence;

import com.honya.bookstore.article.domain.Article;
import com.honya.bookstore.article.domain.ArticleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> {
    Page<Article> findByStatus(ArticleStatus status, Pageable pageable);
    Optional<Article> findBySlugAndStatus(String slug, ArticleStatus status);
}