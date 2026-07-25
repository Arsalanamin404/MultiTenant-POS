package com.arsalan.tenanttable.expenses.service;

import com.arsalan.tenanttable.expenses.dto.CreateExpenseCategoryRequestDto;
import com.arsalan.tenanttable.expenses.dto.ExpenseCategoryResponseDto;
import com.arsalan.tenanttable.expenses.dto.UpdateExpenseCategoryRequestDto;

import java.util.List;
import java.util.UUID;

public interface IExpenseCategoryService {
    ExpenseCategoryResponseDto createCategory(CreateExpenseCategoryRequestDto dto);

    List<ExpenseCategoryResponseDto> getCategories();

    ExpenseCategoryResponseDto updateCategory(
            UUID categoryId,
            UpdateExpenseCategoryRequestDto dto
    );

    ExpenseCategoryResponseDto archiveCategory(UUID categoryId);
}
