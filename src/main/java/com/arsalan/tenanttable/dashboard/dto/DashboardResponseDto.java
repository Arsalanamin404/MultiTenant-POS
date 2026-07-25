package com.arsalan.tenanttable.dashboard.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class DashboardResponseDto {

    // Sales
    private BigDecimal overallRevenue;
    private BigDecimal todayRevenue;
    private BigDecimal monthlyRevenue;

    // Orders
    private Long totalOrders;
    private Long todayOrders;
    private Long pendingOrders;
    private Long completedOrders;
    private Long cancelledOrders;

    // Expenses
    private BigDecimal overallExpenses;
    private BigDecimal todayExpenses;
    private BigDecimal monthlyExpenses;

    // Profit
    private BigDecimal overallProfit;
    private BigDecimal todayProfit;
    private BigDecimal monthlyProfit;

    // Tables
    private Long occupiedTables;
    private Long availableTables;
    private Long totalTables;

    // Staff
    private Long totalStaff;
    private Long activeStaff;

//    private Long activeCoupons;
}