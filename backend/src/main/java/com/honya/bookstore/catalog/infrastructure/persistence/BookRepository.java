package com.honya.bookstore.catalog.infrastructure.persistence;

import com.honya.bookstore.catalog.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
}