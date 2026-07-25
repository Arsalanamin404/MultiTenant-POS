package com.arsalan.tenanttable.expenses.mapper;

import com.arsalan.tenanttable.expenses.dto.ExpenseCategoryResponseDto;
import com.arsalan.tenanttable.expenses.entity.ExpenseCategory;

public final class ExpenseCategoryMapper {
    private ExpenseCategoryMapper() {
    }

    public static ExpenseCategoryResponseDto toDto(ExpenseCategory category) {
        return ExpenseCategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.isActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
