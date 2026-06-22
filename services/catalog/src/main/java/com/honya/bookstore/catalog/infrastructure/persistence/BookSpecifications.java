package com.honya.bookstore.catalog.infrastructure.persistence;

import com.honya.bookstore.catalog.domain.Book;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public final class BookSpecifications {
    private BookSpecifications() {
    }

    public static Specification<Book> minPrice(Integer minPrice) {
        return (root, query, cb) -> minPrice == null ? null : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Book> maxPrice(Integer maxPrice) {
        return (root, query, cb) -> maxPrice == null ? null : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Book> publisher(String publisher) {
        return (root, query, cb) -> {
            if (publisher == null || publisher.isBlank()) {
                return null;
            }
            return cb.equal(root.get("publisher"), publisher);
        };
    }

    public static Specification<Book> year(Integer year) {
        return (root, query, cb) -> year == null ? null : cb.equal(root.get("yearPublished"), year);
    }

    public static Specification<Book> categoryIdsAny(List<UUID> categoryIds) {
        return (root, query, cb) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return null;
            }
            query.distinct(true);
            return root.join("categories").get("id").in(categoryIds);
        };
    }

    public static Specification<Book> search(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return null;
            }

            String pattern = "%" + search.trim().toLowerCase() + "%";
            Predicate title = cb.like(cb.lower(root.get("title")), pattern);
            Predicate author = cb.like(cb.lower(root.get("author")), pattern);
            Predicate publisher = cb.like(cb.lower(root.get("publisher")), pattern);
            return cb.or(title, author, publisher);
        };
    }
}
