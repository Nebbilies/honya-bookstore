package com.honya.bookstore.catalog.infrastructure.persistence;

import com.honya.bookstore.catalog.domain.BookMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookMediaRepository extends JpaRepository<BookMedia, UUID> {
    List<BookMedia> findByBookId(UUID bookId);

    void deleteByBookId(UUID bookId);
}