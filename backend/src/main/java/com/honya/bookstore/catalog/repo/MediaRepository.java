package com.honya.bookstore.catalog.repo;

import com.honya.bookstore.catalog.domain.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {
}