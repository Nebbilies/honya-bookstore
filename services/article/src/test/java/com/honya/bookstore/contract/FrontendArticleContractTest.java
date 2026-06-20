package com.honya.bookstore.contract;

import com.honya.bookstore.article.application.ArticleService;
import com.honya.bookstore.article.domain.Article;
import com.honya.bookstore.article.domain.ArticleStatus;
import com.honya.bookstore.article.web.ArticleController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FrontendArticleContractTest {

    private MockMvc mockMvc;
    private ArticleService articleService;

    @BeforeEach
    void setUp() {
        articleService = mock(ArticleService.class);
        ArticleController controller = new ArticleController(articleService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getPublishedArticles_returns_data_meta_and_article_fields() throws Exception {
        Page<Article> page = new PageImpl<>(List.of(sampleArticle()));
        when(articleService.getPublishedArticles(any())).thenReturn(page);

        mockMvc.perform(get("/api/articles/public?page=1&limit=9"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.meta.totalItems").exists())
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].slug").value("hello-world"))
                .andExpect(jsonPath("$.data[0].title").value("Hello World"))
                .andExpect(jsonPath("$.data[0].status").value("PUBLISHED"))
                .andExpect(jsonPath("$.data[0].thumbnailUrl").isString())
                .andExpect(jsonPath("$.data[0].createdAt").exists());
    }

    @Test
    void getPublishedArticleBySlug_returns_article_shape() throws Exception {
        when(articleService.getPublishedArticleBySlug(eq("hello-world"))).thenReturn(sampleArticle());

        mockMvc.perform(get("/api/articles/public/hello-world"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.slug").value("hello-world"))
                .andExpect(jsonPath("$.title").value("Hello World"))
                .andExpect(jsonPath("$.status").value("PUBLISHED"))
                .andExpect(jsonPath("$.thumbnailUrl").isString());
    }

    private Article sampleArticle() {
        return Article.builder()
                .id(UUID.randomUUID())
                .slug("hello-world")
                .title("Hello World")
                .content("body")
                .tags(List.of("news"))
                .authorId(UUID.randomUUID())
                .thumbnailId(UUID.randomUUID())
                .thumbnailUrl("http://localhost:9000/media/books/cover.jpg")
                .status(ArticleStatus.PUBLISHED)
                .createdAt(OffsetDateTime.parse("2026-05-04T10:15:30Z"))
                .updatedAt(OffsetDateTime.parse("2026-05-04T10:15:30Z"))
                .build();
    }
}
