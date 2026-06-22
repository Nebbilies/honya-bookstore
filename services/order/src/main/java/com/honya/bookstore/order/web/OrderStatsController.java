package com.honya.bookstore.order.web;

import com.honya.bookstore.order.api.BestSellerStat;
import com.honya.bookstore.order.api.MonthlyPoint;
import com.honya.bookstore.order.api.OrderStatsApi;
import com.honya.bookstore.order.api.RecentOrderStat;
import com.honya.bookstore.order.api.StatsPeriod;
import com.honya.bookstore.security.StaffOrAdmin;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders/stats")
@RequiredArgsConstructor
@StaffOrAdmin
public class OrderStatsController {

    private final OrderStatsApi orderStatsApi;

    @GetMapping("/sales-this-month")
    public long salesThisMonth() {
        return orderStatsApi.salesThisMonth();
    }

    @GetMapping("/orders-this-month")
    public long ordersThisMonth() {
        return orderStatsApi.ordersThisMonth();
    }

    @GetMapping("/new-customers-this-month")
    public long newCustomersThisMonth() {
        return orderStatsApi.newCustomersThisMonth();
    }

    @GetMapping("/revenue-per-year")
    public List<MonthlyPoint> revenuePerYear(@RequestParam int year) {
        return orderStatsApi.revenuePerYear(year);
    }

    @GetMapping("/orders-per-year")
    public List<MonthlyPoint> ordersPerYear(@RequestParam int year) {
        return orderStatsApi.ordersPerYear(year);
    }

    @GetMapping("/best-sellers")
    public List<BestSellerStat> bestSellers(@RequestParam StatsPeriod period, @RequestParam int limit) {
        return orderStatsApi.bestSellers(period, limit);
    }

    @GetMapping("/recent-orders")
    public List<RecentOrderStat> recentOrders(@RequestParam int limit) {
        return orderStatsApi.recentOrders(limit);
    }
}
