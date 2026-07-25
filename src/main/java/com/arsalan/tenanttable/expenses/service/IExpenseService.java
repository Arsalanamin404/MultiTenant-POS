package com.arsalan.tenanttable.expenses.service;

import com.arsalan.tenanttable.expenses.dto.CreateExpenseRequestDto;
import com.arsalan.tenanttable.expenses.dto.ExpenseResponseDto;
import com.arsalan.tenanttable.expenses.dto.UpdateExpenseRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IExpenseService {
    ExpenseResponseDto createExpense(CreateExpenseRequestDto dto);

    Page<ExpenseResponseDto> getExpenses(Pageable pageable);

    ExpenseResponseDto getExpense(UUID expenseId);

    ExpenseResponseDto updateExpense(
            UUID expenseId,
            UpdateExpenseRequestDto dto
    );

    ExpenseResponseDto deleteExpense(UUID expenseId);

}
