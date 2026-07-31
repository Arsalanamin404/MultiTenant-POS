package com.arsalan.tenanttable.administration.tenant.dto;

import lombok.Builder;

@Builder
public record BusinessStatisticsDto(
        long totalUsers,
        long totalCategories,
        long totalMenuItems,
        long totalDiningTables,
        long totalOrders
) {
}
