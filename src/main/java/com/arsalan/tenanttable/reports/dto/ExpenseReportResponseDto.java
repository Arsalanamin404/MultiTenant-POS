package com.arsalan.tenanttable.reports.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class ExpenseReportResponseDto {
    private LocalDate fromDate;
    private LocalDate toDate;

    private BigDecimal totalExpenses;

    private List<ExpenseCategorySummaryDto> categoryBreakdown;
}