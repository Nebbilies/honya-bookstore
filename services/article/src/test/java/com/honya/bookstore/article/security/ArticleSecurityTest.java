package com.honya.bookstore.article.security;

import com.honya.bookstore.article.application.ArticleService;
import com.honya.bookstore.article.config.ArticlePublicEndpoints;
import com.honya.platform.security.HonyaResourceServerSecurity;
import com.honya.bookstore.article.domain.Article;
import com.honya.bookstore.article.domain.ArticleStatus;
import com.honya.bookstore.article.web.ArticleController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticleController.class)
@Import({HonyaResourceServerSecurity.class, ArticlePublicEndpoints.class})
class ArticleSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArticleService articleService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void publicArticlesAreAccessibleWithoutToken() throws Exception {
        when(articleService.getPublishedArticles(any())).thenReturn(emptyPage());

        mockMvc.perform(get("/api/articles/public?page=1&limit=9"))
                .andExpect(status().isOk());
    }

    @Test
    void listAllArticlesWithAdminRoleShouldSucceed() throws Exception {
        when(jwtDecoder.decode("admin-token")).thenReturn(buildToken("admin-token", "ADMIN", UUID.randomUUID().toString()));
        when(articleService.getAllArticles(any())).thenReturn(emptyPage());

        mockMvc.perform(get("/api/articles")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer admin-token"))
                .andExpect(status().isOk());
    }

    @Test
    void listAllArticlesWithCustomerRoleShouldBeForbidden() throws Exception {
        when(jwtDecoder.decode("customer-token")).thenReturn(buildToken("customer-token", "CUSTOMER", UUID.randomUUID().toString()));

        mockMvc.perform(get("/api/articles")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer customer-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void listAllArticlesWithoutTokenShouldBeUnauthorized() throws Exception {
        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createArticleWithAdminBearerShouldSucceedWithoutCsrfToken() throws Exception {
        when(jwtDecoder.decode("admin-token")).thenReturn(buildToken("admin-token", "ADMIN", UUID.randomUUID().toString()));
        when(articleService.createArticle(any())).thenReturn(Article.builder()
                .id(UUID.randomUUID())
                .title("t")
                .slug("s")
                .status(ArticleStatus.DRAFT)
                .build());

        mockMvc.perform(post("/api/articles")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"t","slug":"s","content":"c","status":"DRAFT","tags":["news"]}
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void createArticleWithCustomerRoleShouldBeForbidden() throws Exception {
        when(jwtDecoder.decode("customer-token")).thenReturn(buildToken("customer-token", "CUSTOMER", UUID.randomUUID().toString()));

        mockMvc.perform(post("/api/articles")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer customer-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"t","slug":"s","content":"c","status":"DRAFT"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void createArticleWithHttpBasicShouldBeUnauthorized() throws Exception {
        mockMvc.perform(post("/api/articles")
                        .with(httpBasic("admin", "123456"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"t","slug":"s","content":"c","status":"DRAFT"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    private Page<Article> emptyPage() {
        return new PageImpl<>(List.of());
    }

    private Jwt buildToken(String tokenValue, String role, String subject) {
        return Jwt.withTokenValue(tokenValue)
                .header("alg", "RS256")
                .claim("sub", subject)
                .claim("aud", List.of("honya-api"))
                .claim("realm_access", Map.of("roles", List.of(role)))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }
}
