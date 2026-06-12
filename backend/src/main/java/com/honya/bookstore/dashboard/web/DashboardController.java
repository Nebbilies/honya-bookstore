package com.honya.bookstore.dashboard.web;

import com.honya.bookstore.dashboard.web.dto.response.BestSellerDTO;
import com.honya.bookstore.dashboard.web.dto.response.DashboardSummaryDTO;
import com.honya.bookstore.dashboard.web.dto.response.MonthlyPointDTO;
import com.honya.bookstore.dashboard.web.dto.response.RecentOrderDTO;
import com.honya.bookstore.order.api.OrderStatsApi;
import com.honya.bookstore.order.api.StatsPeriod;
import com.honya.bookstore.security.StaffOrAdmin;
import com.honya.bookstore.user.api.UserStatsApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.util.List;

@Tag(name = "Dashboard", description = "Aggregate statistics for the CMS dashboard")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@StaffOrAdmin
public class DashboardController {

    private final OrderStatsApi orderStatsApi;
    private final UserStatsApi userStatsApi;

    @Operation(summary = "Dashboard summary", description = "Sales this month, total users, new customers, orders this month")
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO> getSummary() {
        DashboardSummaryDTO summary = new DashboardSummaryDTO(
                orderStatsApi.salesThisMonth(),
                userStatsApi.totalUsers(),
                orderStatsApi.newCustomersThisMonth(),
                orderStatsApi.ordersThisMonth());
        return ResponseEntity.ok(summary);
    }

    @Operation(summary = "Sales per year", description = "Monthly paid revenue for the given year")
    @GetMapping("/sales-per-year")
    public ResponseEntity<List<MonthlyPointDTO>> getSalesPerYear(
            @RequestParam(required = false) Integer year) {
        List<MonthlyPointDTO> points = orderStatsApi.revenuePerYear(resolveYear(year)).stream()
                .map(p -> new MonthlyPointDTO(p.month(), p.value()))
                .toList();
        return ResponseEntity.ok(points);
    }

    @Operation(summary = "Orders per year", description = "Monthly order count for the given year")
    @GetMapping("/orders-per-year")
    public ResponseEntity<List<MonthlyPointDTO>> getOrdersPerYear(
            @RequestParam(required = false) Integer year) {
        List<MonthlyPointDTO> points = orderStatsApi.ordersPerYear(resolveYear(year)).stream()
                .map(p -> new MonthlyPointDTO(p.month(), p.value()))
                .toList();
        return ResponseEntity.ok(points);
    }

    @Operation(summary = "Best sellers", description = "Top sold books in the given period (WEEK, MONTH, YEAR)")
    @GetMapping("/best-sellers")
    public ResponseEntity<List<BestSellerDTO>> getBestSellers(
            @RequestParam(required = false, defaultValue = "YEAR") String period,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        List<BestSellerDTO> sellers = orderStatsApi.bestSellers(parsePeriod(period), limit).stream()
                .map(s -> new BestSellerDTO(s.title(), s.author(), s.totalSold()))
                .toList();
        return ResponseEntity.ok(sellers);
    }

    @Operation(summary = "Recent orders", description = "Latest orders for the orders history feed")
    @GetMapping("/recent-orders")
    public ResponseEntity<List<RecentOrderDTO>> getRecentOrders(
            @RequestParam(required = false, defaultValue = "10") int limit) {
        List<RecentOrderDTO> orders = orderStatsApi.recentOrders(limit).stream()
                .map(o -> new RecentOrderDTO(o.id(), o.createdAt(), o.totalAmount()))
                .toList();
        return ResponseEntity.ok(orders);
    }

    private int resolveYear(Integer year) {
        return year == null ? Year.now().getValue() : year;
    }

    private StatsPeriod parsePeriod(String period) {
        try {
            return StatsPeriod.valueOf(period.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return StatsPeriod.YEAR;
        }
    }
}
