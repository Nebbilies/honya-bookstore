package com.honya.bookstore.article.infrastructure.persistence;

import com.honya.bookstore.article.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
interface ArticleRepository extends JpaRepository<Article, UUID> {
}