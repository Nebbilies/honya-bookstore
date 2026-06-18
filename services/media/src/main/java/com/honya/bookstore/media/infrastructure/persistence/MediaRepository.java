package com.honya.bookstore.media.infrastructure.persistence;

import com.honya.bookstore.media.domain.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {
    List<Media> findByDeletedAtIsNull();
}
