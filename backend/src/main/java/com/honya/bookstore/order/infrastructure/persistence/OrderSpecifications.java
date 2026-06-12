package com.honya.bookstore.order.infrastructure.persistence;

import com.honya.bookstore.order.domain.Order;
import com.honya.bookstore.order.domain.OrderStatus;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;

public final class OrderSpecifications {

    private OrderSpecifications() {
    }

    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Order> recipientContains(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }
            String pattern = "%" + search.trim().toLowerCase() + "%";
            Expression<String> fullName = cb.lower(
                    cb.concat(
                            cb.concat(cb.coalesce(root.get("firstName"), ""), " "),
                            cb.coalesce(root.get("lastName"), "")));
            return cb.like(fullName, pattern);
        };
    }
}
