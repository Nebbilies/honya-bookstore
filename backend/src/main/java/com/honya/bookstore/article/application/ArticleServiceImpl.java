package com.honya.bookstore.article.application;

import com.honya.bookstore.article.domain.Article;
import com.honya.bookstore.article.infrastructure.persistence.ArticleRepository;
import com.honya.bookstore.shared.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    @Override
    public Page<Article> getAllArticles(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    @Override
    public Article getArticleById(UUID id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article ", id));
    }

    @Override
    public Article createArticle(Article article) {
        return articleRepository.save(article);
    }

    @Override
    public Article updateArticle(UUID id, Article article) {
        Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article ", id));

        existingArticle.setTitle(article.getTitle());
        existingArticle.setContent(article.getContent());
        existingArticle.setAuthorId(article.getAuthorId());
        existingArticle.setTags(article.getTags());
        existingArticle.setStatus(article.getStatus());
        existingArticle.setSlug(article.getSlug());
        existingArticle.setThumbnailId(article.getThumbnailId());
        existingArticle.setThumbnailUrl(article.getThumbnailUrl());

        return articleRepository.save(existingArticle);
    }

    @Override
    public void deleteArticle(UUID id) {
        articleRepository.deleteById(id);
    }
}
