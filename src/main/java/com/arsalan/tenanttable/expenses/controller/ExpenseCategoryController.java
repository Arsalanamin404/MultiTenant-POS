package com.arsalan.tenanttable.expenses.controller;

import com.arsalan.tenanttable.common.dto.ApiResponse;
import com.arsalan.tenanttable.expenses.dto.CreateExpenseCategoryRequestDto;
import com.arsalan.tenanttable.expenses.dto.ExpenseCategoryResponseDto;
import com.arsalan.tenanttable.expenses.dto.UpdateExpenseCategoryRequestDto;
import com.arsalan.tenanttable.expenses.service.IExpenseCategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/expense-categories")
@AllArgsConstructor
public class ExpenseCategoryController {
    private IExpenseCategoryService expenseCategoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseCategoryResponseDto>> createCategory(
            @Valid @RequestBody CreateExpenseCategoryRequestDto dto,
            HttpServletRequest request
    ) {

        ExpenseCategoryResponseDto response = expenseCategoryService.createCategory(dto);

        ApiResponse<ExpenseCategoryResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.CREATED.value(),
                "EXPENSE_CATEGORY_CREATED_SUCCESSFULLY",
                response,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseCategoryResponseDto>>> getCategories(
            HttpServletRequest request
    ) {

        List<ExpenseCategoryResponseDto> response = expenseCategoryService.getCategories();

        ApiResponse<List<ExpenseCategoryResponseDto>> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "EXPENSE_CATEGORIES_FETCHED_SUCCESSFULLY",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseCategoryResponseDto>> updateCategory(
            @Valid UUID id,
            @Valid @RequestBody UpdateExpenseCategoryRequestDto dto,
            HttpServletRequest request
    ) {

        ExpenseCategoryResponseDto response = expenseCategoryService.updateCategory(id, dto);

        ApiResponse<ExpenseCategoryResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "EXPENSE_CATEGORY_UPDATED_SUCCESSFULLY",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<ExpenseCategoryResponseDto>> archiveCategory(
            @Valid UUID id,
            HttpServletRequest request
    ) {

        ExpenseCategoryResponseDto response = expenseCategoryService.archiveCategory(id);

        ApiResponse<ExpenseCategoryResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "EXPENSE_CATEGORY_ARCHIVED_SUCCESSFULLY",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }
}
