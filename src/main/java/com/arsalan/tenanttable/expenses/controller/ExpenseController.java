package com.arsalan.tenanttable.expenses.controller;

import com.arsalan.tenanttable.common.dto.ApiResponse;
import com.arsalan.tenanttable.expenses.dto.CreateExpenseRequestDto;
import com.arsalan.tenanttable.expenses.dto.ExpenseResponseDto;
import com.arsalan.tenanttable.expenses.dto.UpdateExpenseRequestDto;
import com.arsalan.tenanttable.expenses.service.IExpenseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/expenses")
@AllArgsConstructor
public class ExpenseController {
    private IExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponseDto>> createExpense(
            @Valid @RequestBody CreateExpenseRequestDto dto,
            HttpServletRequest request
    ) {

        ExpenseResponseDto response = expenseService.createExpense(dto);

        ApiResponse<ExpenseResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.CREATED.value(),
                "EXPENSE_CREATED_SUCCESSFULLY",
                response,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ExpenseResponseDto>>> getExpenses(
            @PageableDefault(size = 8, sort = "expenseDate", direction = Sort.Direction.DESC) Pageable pageable,
            HttpServletRequest request
    ) {

        Page<ExpenseResponseDto> response = expenseService.getExpenses(pageable);

        ApiResponse<Page<ExpenseResponseDto>> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "EXPENSES_FETCHED_SUCCESSFULLY",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponseDto>> getExpense(
            @Valid UUID id,
            HttpServletRequest request
    ) {

        ExpenseResponseDto response = expenseService.getExpense(id);

        ApiResponse<ExpenseResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "EXPENSE_FETCHED_SUCCESSFULLY",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponseDto>> updateExpense(
            @Valid UUID id,
            @Valid @RequestBody UpdateExpenseRequestDto dto,
            HttpServletRequest request
    ) {

        ExpenseResponseDto response = expenseService.updateExpense(id, dto);

        ApiResponse<ExpenseResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "EXPENSE_UPDATED_SUCCESSFULLY",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponseDto>> deleteExpense(
            @Valid UUID id,
            HttpServletRequest request
    ) {

        ExpenseResponseDto response = expenseService.deleteExpense(id);

        ApiResponse<ExpenseResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "EXPENSE_DELETED_SUCCESSFULLY",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }
}
