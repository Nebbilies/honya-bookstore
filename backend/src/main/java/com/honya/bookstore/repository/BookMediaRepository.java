package com.honya.bookstore.repository;

import com.honya.bookstore.domain.entity.BookMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface BookMediaRepository extends JpaRepository<BookMedia, UUID> {
}