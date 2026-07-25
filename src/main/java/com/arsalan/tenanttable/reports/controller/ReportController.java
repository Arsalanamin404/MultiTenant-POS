package com.arsalan.tenanttable.reports.controller;

import com.arsalan.tenanttable.common.dto.ApiResponse;
import com.arsalan.tenanttable.reports.dto.*;
import com.arsalan.tenanttable.reports.service.IReportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
public class ReportController {
    private final IReportService reportService;

    @GetMapping("/sales")
    public ResponseEntity<ApiResponse<SalesReportResponseDto>> getSalesReport(
            @Valid ReportFilterRequestDto filter,
            HttpServletRequest request
    ) {
        SalesReportResponseDto response = reportService.getSalesReport(filter.getFromDate(), filter.getToDate());

        ApiResponse<SalesReportResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Sales report retrieved successfully.",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/expenses")
    public ResponseEntity<ApiResponse<ExpenseReportResponseDto>> getExpensesReport(
            @Valid ReportFilterRequestDto filter,
            HttpServletRequest request
    ) {
        ExpenseReportResponseDto response = reportService.getExpenseReport(filter.getFromDate(), filter.getToDate());

        ApiResponse<ExpenseReportResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Expense report retrieved successfully.",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/profit")
    public ResponseEntity<ApiResponse<ProfitReportResponseDto>> getProfitReport(
            @Valid ReportFilterRequestDto filter,
            HttpServletRequest request
    ) {
        ProfitReportResponseDto response = reportService.getProfitReport(filter.getFromDate(), filter.getToDate());

        ApiResponse<ProfitReportResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Profit report retrieved successfully.",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/order")
    public ResponseEntity<ApiResponse<OrderReportResponseDto>> getOrderReport(
            @Valid ReportFilterRequestDto filter,
            HttpServletRequest request
    ) {
        OrderReportResponseDto response = reportService.getOrderReport(filter.getFromDate(), filter.getToDate());

        ApiResponse<OrderReportResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Order report retrieved successfully.",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }
}
