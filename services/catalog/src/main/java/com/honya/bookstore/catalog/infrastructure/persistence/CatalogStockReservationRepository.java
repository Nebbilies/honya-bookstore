package com.honya.bookstore.catalog.infrastructure.persistence;

import com.honya.bookstore.catalog.domain.CatalogStockReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface CatalogStockReservationRepository extends JpaRepository<CatalogStockReservation, UUID> {

    @Modifying
    @Query(value = "INSERT INTO catalog.catalog_stock_reservations (id, saga_id, book_id, quantity, status, created_at, updated_at) "
            + "VALUES (gen_random_uuid(), :sagaId, :bookId, :quantity, 'RESERVED', :now, :now) "
            + "ON CONFLICT (saga_id, book_id) DO NOTHING", nativeQuery = true)
    int insertIfAbsent(@Param("sagaId") UUID sagaId,
                       @Param("bookId") UUID bookId,
                       @Param("quantity") Integer quantity,
                       @Param("now") OffsetDateTime now);

    @Modifying
    @Query(value = "UPDATE catalog.catalog_stock_reservations SET status = 'RELEASED', updated_at = now() "
            + "WHERE saga_id = :sagaId AND book_id = :bookId AND status = 'RESERVED'", nativeQuery = true)
    int markReleasedIfReserved(@Param("sagaId") UUID sagaId, @Param("bookId") UUID bookId);
}
