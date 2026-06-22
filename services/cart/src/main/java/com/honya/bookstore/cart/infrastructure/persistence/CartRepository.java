package com.honya.bookstore.cart.infrastructure.persistence;

import com.honya.bookstore.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

    Optional<Cart> findByOwnerId(UUID ownerId);

    @Query("""
            select distinct cart from Cart cart
            join fetch cart.items item
            where item.catalogItemId = :catalogItemId
            """)
    List<Cart> findAllByCatalogItemId(@Param("catalogItemId") UUID catalogItemId);
}