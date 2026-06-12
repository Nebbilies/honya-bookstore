package com.honya.bookstore.dashboard.web.dto.response;

public record DashboardSummaryDTO(
        long salesThisMonth,
        long totalUsers,
        long newCustomers,
        long ordersThisMonth) {
}
