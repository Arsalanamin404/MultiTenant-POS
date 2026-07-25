package com.arsalan.tenanttable.reports.service;

import com.arsalan.tenanttable.reports.dto.ExpenseReportResponseDto;
import com.arsalan.tenanttable.reports.dto.OrderReportResponseDto;
import com.arsalan.tenanttable.reports.dto.ProfitReportResponseDto;
import com.arsalan.tenanttable.reports.dto.SalesReportResponseDto;

import java.time.LocalDate;

public interface IReportService {
    SalesReportResponseDto getSalesReport(LocalDate fromDate, LocalDate toDate);

    ExpenseReportResponseDto getExpenseReport(LocalDate fromDate, LocalDate toDate);

    ProfitReportResponseDto getProfitReport(LocalDate fromDate, LocalDate toDate);

    OrderReportResponseDto getOrderReport(LocalDate fromDate, LocalDate toDate);
}
