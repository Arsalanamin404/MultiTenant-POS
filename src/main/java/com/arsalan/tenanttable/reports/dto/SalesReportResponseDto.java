package com.arsalan.tenanttable.reports.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@Data
public class SalesReportResponseDto {
    private Instant fromDate;

    private Instant toDate;

    private BigDecimal totalRevenue;

    private Long totalOrders;

    private BigDecimal averageOrderValue;

    private BigDecimal cashCollection;

    private BigDecimal cardCollection;

    private BigDecimal upiCollection;
}
