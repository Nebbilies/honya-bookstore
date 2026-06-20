package com.honya.bookstore.article.application;

import com.honya.bookstore.article.domain.ArticleStatus;
import com.honya.bookstore.article.infrastructure.persistence.ArticleRepository;
import com.honya.bookstore.shared.error.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ArticleServiceImplErrorTest {

    private final ArticleRepository articleRepository = mock(ArticleRepository.class);
    private final ArticleServiceImpl service = new ArticleServiceImpl(articleRepository);

    @Test
    void getArticleByIdThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(articleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getArticleById(id));
    }

    @Test
    void getPublishedArticleBySlugThrowsWhenMissing() {
        when(articleRepository.findBySlugAndStatus(eq("missing"), eq(ArticleStatus.PUBLISHED)))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getPublishedArticleBySlug("missing"));
    }

    @Test
    void updateArticleThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(articleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateArticle(id, null));
    }
}
