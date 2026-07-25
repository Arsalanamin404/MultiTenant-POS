package com.arsalan.tenanttable.expenses.mapper;

import com.arsalan.tenanttable.expenses.dto.ExpenseResponseDto;
import com.arsalan.tenanttable.expenses.entity.Expense;

public final class ExpenseMapper {
    private ExpenseMapper() {
    }

    public static ExpenseResponseDto toDto(Expense expense) {
        return ExpenseResponseDto.builder()
                .id(expense.getId())
                .categoryId(expense.getCategory().getId())
                .categoryName(expense.getCategory().getName())
                .title(expense.getTitle())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .expenseDate(expense.getExpenseDate())
                .paymentMethod(expense.getPaymentMethod())
                .createdById(expense.getCreatedBy().getId())
                .createdByName(expense.getCreatedBy().getFullName())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }
}
