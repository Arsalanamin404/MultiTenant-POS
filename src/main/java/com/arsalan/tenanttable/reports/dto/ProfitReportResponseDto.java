package com.arsalan.tenanttable.reports.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
public class ProfitReportResponseDto {

    private LocalDate fromDate;

    private LocalDate toDate;

    private BigDecimal totalRevenue;

    private BigDecimal totalExpenses;

    private BigDecimal netProfit;
}
