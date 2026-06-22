package com.honya.bookstore.order.api;

import com.honya.bookstore.order.infrastructure.persistence.BestSellerAggregate;
import com.honya.bookstore.order.infrastructure.persistence.MonthlyAggregate;
import com.honya.bookstore.order.infrastructure.persistence.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderStatsApiAdapter implements OrderStatsApi {

    private final OrderRepository orderRepository;

    @Override
    public long salesThisMonth() {
        OffsetDateTime start = startOfCurrentMonth();
        return orderRepository.sumPaidRevenueBetween(start, start.plusMonths(1));
    }

    @Override
    public long ordersThisMonth() {
        OffsetDateTime start = startOfCurrentMonth();
        return orderRepository.countOrdersBetween(start, start.plusMonths(1));
    }

    @Override
    public long newCustomersThisMonth() {
        OffsetDateTime start = startOfCurrentMonth();
        return orderRepository.countFirstTimeBuyersBetween(start, start.plusMonths(1));
    }

    @Override
    public List<MonthlyPoint> revenuePerYear(int year) {
        return toTwelveMonths(orderRepository.revenueByMonth(year));
    }

    @Override
    public List<MonthlyPoint> ordersPerYear(int year) {
        return toTwelveMonths(orderRepository.ordersByMonth(year));
    }

    @Override
    public List<BestSellerStat> bestSellers(StatsPeriod period, int limit) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime start = switch (period) {
            case WEEK -> now.minusWeeks(1);
            case MONTH -> now.minusMonths(1);
            case YEAR -> now.minusYears(1);
        };
        return orderRepository.topBestSellers(start, now, limit).stream()
                .map(this::toBestSellerStat)
                .toList();
    }

    @Override
    public List<RecentOrderStat> recentOrders(int limit) {
        return orderRepository.findByOrderByCreatedAtDesc(PageRequest.of(0, limit)).stream()
                .map(order -> new RecentOrderStat(order.getId(), order.getCreatedAt(), order.getTotalAmount()))
                .toList();
    }

    private OffsetDateTime startOfCurrentMonth() {
        return OffsetDateTime.now(ZoneOffset.UTC)
                .withDayOfMonth(1)
                .truncatedTo(ChronoUnit.DAYS);
    }

    private List<MonthlyPoint> toTwelveMonths(List<MonthlyAggregate> rows) {
        long[] values = new long[12];
        for (MonthlyAggregate row : rows) {
            if (row.getMonth() != null && row.getMonth() >= 1 && row.getMonth() <= 12) {
                values[row.getMonth() - 1] = row.getValue() == null ? 0L : row.getValue();
            }
        }
        List<MonthlyPoint> points = new ArrayList<>(12);
        for (int month = 1; month <= 12; month++) {
            points.add(new MonthlyPoint(month, values[month - 1]));
        }
        return points;
    }

    private BestSellerStat toBestSellerStat(BestSellerAggregate row) {
        return new BestSellerStat(
                row.getTitle(),
                row.getAuthor(),
                row.getTotalSold() == null ? 0L : row.getTotalSold());
    }
}
