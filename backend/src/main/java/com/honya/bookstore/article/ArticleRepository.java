package com.honya.bookstore.article;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
interface ArticleRepository extends JpaRepository<Article, UUID> {
}