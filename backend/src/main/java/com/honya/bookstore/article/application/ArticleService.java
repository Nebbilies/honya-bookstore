package com.honya.bookstore.article.application;

import com.honya.bookstore.article.domain.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ArticleService {
    Page<Article> getAllArticles(Pageable pageable);
    Page<Article> getPublishedArticles(Pageable pageable);
    Article getPublishedArticleBySlug(String slug);
    Article getArticleById(UUID id);
    Article createArticle(Article article);
    Article updateArticle(UUID id, Article article);
    void deleteArticle(UUID id);
}
