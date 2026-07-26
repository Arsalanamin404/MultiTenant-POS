package com.arsalan.tenanttable.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class ExpenseCategorySummaryDto {
    private UUID categoryId;
    private String categoryName;
    private BigDecimal amount;
}
