package com.arsalan.tenanttable.reports.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
public class OrderReportResponseDto {
    private LocalDate fromDate;
    private LocalDate toDate;

    private Long totalOrders;

    private Long completedOrders;

    private Long inProgressOrders;

    private Long cancelledOrders;

    private Long rejectedOrders;

    private BigDecimal completionRate;

    private BigDecimal cancellationRate;

    private BigDecimal rejectionRate;
}