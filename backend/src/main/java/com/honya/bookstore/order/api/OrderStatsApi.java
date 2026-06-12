package com.honya.bookstore.order.api;

import java.util.List;

public interface OrderStatsApi {
    long salesThisMonth();
    long ordersThisMonth();
    long newCustomersThisMonth();
    List<MonthlyPoint> revenuePerYear(int year);
    List<MonthlyPoint> ordersPerYear(int year);
    List<BestSellerStat> bestSellers(StatsPeriod period, int limit);
    List<RecentOrderStat> recentOrders(int limit);
}
