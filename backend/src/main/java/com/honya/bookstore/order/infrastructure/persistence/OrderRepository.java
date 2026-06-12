package com.honya.bookstore.order.infrastructure.persistence;

import com.honya.bookstore.order.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Page<Order> findByUserId(UUID userId, Pageable pageable);

    @Query("""
            SELECT COALESCE(SUM(o.totalAmount), 0)
            FROM Order o
            WHERE o.isPaid = true AND o.paidAt >= :start AND o.paidAt < :end
            """)
    long sumPaidRevenueBetween(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);

    @Query("""
            SELECT COUNT(o)
            FROM Order o
            WHERE o.createdAt >= :start AND o.createdAt < :end
              AND o.status <> com.honya.bookstore.order.domain.OrderStatus.CANCELLED
            """)
    long countOrdersBetween(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);

    @Query(value = """
            SELECT CAST(EXTRACT(MONTH FROM paid_at) AS int) AS month,
                   COALESCE(SUM(total_amount), 0) AS value
            FROM "order".orders
            WHERE is_paid = true AND EXTRACT(YEAR FROM paid_at) = :year
            GROUP BY EXTRACT(MONTH FROM paid_at)
            """, nativeQuery = true)
    List<MonthlyAggregate> revenueByMonth(@Param("year") int year);

    @Query(value = """
            SELECT CAST(EXTRACT(MONTH FROM created_at) AS int) AS month,
                   COUNT(*) AS value
            FROM "order".orders
            WHERE EXTRACT(YEAR FROM created_at) = :year AND status <> 'CANCELLED'
            GROUP BY EXTRACT(MONTH FROM created_at)
            """, nativeQuery = true)
    List<MonthlyAggregate> ordersByMonth(@Param("year") int year);

    @Query(value = """
            SELECT COUNT(*) FROM (
                SELECT user_id
                FROM "order".orders
                WHERE user_id IS NOT NULL
                GROUP BY user_id
                HAVING MIN(created_at) >= :start AND MIN(created_at) < :end
            ) AS first_orders
            """, nativeQuery = true)
    long countFirstTimeBuyersBetween(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);

    @Query(value = """
            SELECT b.title AS title, b.author AS author,
                   COALESCE(SUM(oi.quantity), 0) AS totalSold
            FROM "order".order_items oi
            JOIN "order".orders o ON o.id = oi.order_id
            JOIN "order".order_items_books b ON b.id = oi.book_id
            WHERE o.created_at >= :start AND o.created_at < :end AND o.status <> 'CANCELLED'
            GROUP BY b.title, b.author
            ORDER BY totalSold DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<BestSellerAggregate> topBestSellers(@Param("start") OffsetDateTime start,
                                             @Param("end") OffsetDateTime end,
                                             @Param("limit") int limit);

    List<Order> findByOrderByCreatedAtDesc(Pageable pageable);
}
