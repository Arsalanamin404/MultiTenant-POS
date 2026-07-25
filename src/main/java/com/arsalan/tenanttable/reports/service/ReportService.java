package com.arsalan.tenanttable.reports.service;

import com.arsalan.tenanttable.common.utils.DateRangeUtil;
import com.arsalan.tenanttable.common.utils.ICurrentUserUtilService;
import com.arsalan.tenanttable.exception.InvalidOperationException;
import com.arsalan.tenanttable.exception.ResourceNotFoundException;
import com.arsalan.tenanttable.expenses.repository.ExpenseRepository;
import com.arsalan.tenanttable.order.enums.OrderStatus;
import com.arsalan.tenanttable.order.repository.OrderRepository;
import com.arsalan.tenanttable.payment.enums.PaymentMethod;
import com.arsalan.tenanttable.payment.repository.PaymentRepository;
import com.arsalan.tenanttable.reports.dto.*;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReportService implements IReportService {

    private final TenantRepository tenantRepository;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ExpenseRepository expenseRepository;
    private final ICurrentUserUtilService currentUserUtilService;

    private Tenant getCurrentTenant() {
        UUID tenantId = currentUserUtilService.getCurrentTenantId();

        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("TENANT_NOT_FOUND"));
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null) {
            throw new InvalidOperationException("Date range is required.");
        }

        if (fromDate.isAfter(toDate)) {
            throw new InvalidOperationException("From date cannot be after to date.");
        }
    }

    private BigDecimal calculateRate(long value, long total) {
        if (total == 0) return BigDecimal.ZERO;

        return BigDecimal.valueOf(value)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
    }

    private static final List<OrderStatus> ACTIVE_ORDER_STATUSES = List.of(
            OrderStatus.PENDING,
            OrderStatus.CONFIRMED,
            OrderStatus.PREPARING,
            OrderStatus.READY,
            OrderStatus.SERVED
    );

    @Override
    public SalesReportResponseDto getSalesReport(LocalDate fromDate, LocalDate toDate) {
        validateDateRange(fromDate, toDate);

        UUID tenantId = getCurrentTenant().getId();

        Instant start = DateRangeUtil.startOfDay(fromDate);
        Instant end = DateRangeUtil.startOfDay(toDate.plusDays(1));

        BigDecimal totalRevenue = paymentRepository.sumRevenueBetween(tenantId, start, end);
        long totalOrders = orderRepository.countOrdersBetween(tenantId, start, end);

        BigDecimal averageOrderValue = totalOrders == 0
                ? BigDecimal.ZERO :
                totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP);

        BigDecimal cashCollection = paymentRepository.sumRevenueByPaymentMethodBetween(
                tenantId,
                PaymentMethod.CASH,
                start,
                end
        );

        BigDecimal cardCollection = paymentRepository.sumRevenueByPaymentMethodBetween(
                tenantId,
                PaymentMethod.CARD,
                start,
                end
        );

        BigDecimal upiCollection = paymentRepository.sumRevenueByPaymentMethodBetween(
                tenantId,
                PaymentMethod.UPI,
                start,
                end
        );

        return SalesReportResponseDto.builder()
                .fromDate(start)
                .toDate(end)
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .averageOrderValue(averageOrderValue)
                .cashCollection(cashCollection)
                .cardCollection(cardCollection)
                .upiCollection(upiCollection)
                .build();
    }

    @Override
    public ExpenseReportResponseDto getExpenseReport(LocalDate fromDate, LocalDate toDate) {
        validateDateRange(fromDate, toDate);

        UUID tenantId = getCurrentTenant().getId();

        BigDecimal totalExpenses = expenseRepository.getExpensesBetween(
                tenantId,
                fromDate,
                toDate.plusDays(1)
        );

        List<ExpenseCategorySummaryDto> expensesByCategory = expenseRepository.getExpenseCategorySummary(
                tenantId,
                fromDate,
                toDate.plusDays(1)
        );

        return ExpenseReportResponseDto.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .totalExpenses(totalExpenses)
                .categoryBreakdown(expensesByCategory)
                .build();
    }

    @Override
    public ProfitReportResponseDto getProfitReport(LocalDate fromDate, LocalDate toDate) {
        validateDateRange(fromDate, toDate);

        UUID tenantId = getCurrentTenant().getId();

        Instant start = DateRangeUtil.startOfDay(fromDate);
        Instant end = DateRangeUtil.startOfNextDay(toDate);

        BigDecimal totalRevenue = paymentRepository.sumRevenueBetween(tenantId, start, end);
        BigDecimal totalExpenses = expenseRepository.getExpensesBetween(tenantId, fromDate, toDate.plusDays(1));

        BigDecimal netProfit = totalRevenue.subtract(totalExpenses);

        return ProfitReportResponseDto.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .totalRevenue(totalRevenue)
                .totalExpenses(totalExpenses)
                .netProfit(netProfit)
                .build();
    }

    @Override
    public OrderReportResponseDto getOrderReport(LocalDate fromDate, LocalDate toDate) {
        validateDateRange(fromDate, toDate);

        UUID tenantId = getCurrentTenant().getId();

        Instant start = DateRangeUtil.startOfDay(fromDate);
        Instant end = DateRangeUtil.startOfNextDay(toDate);

        long totalOrders = orderRepository.countOrdersBetween(tenantId, start, end);
        long completedOrders = orderRepository.countOrdersBetweenAndStatus(tenantId, OrderStatus.COMPLETED, start, end);
        long cancelledOrders = orderRepository.countOrdersBetweenAndStatus(tenantId, OrderStatus.CANCELLED, start, end);
        long rejectedOrders = orderRepository.countOrdersBetweenAndStatus(tenantId, OrderStatus.REJECTED, start, end);
        long inProgressOrders = orderRepository.countOrdersBetweenAndStatuses(
                tenantId,
                ACTIVE_ORDER_STATUSES,
                start,
                end
        );
        return OrderReportResponseDto.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .totalOrders(totalOrders)
                .completedOrders(completedOrders)
                .inProgressOrders(inProgressOrders)
                .cancelledOrders(cancelledOrders)
                .rejectedOrders(rejectedOrders)
                .completionRate(calculateRate(completedOrders, totalOrders))
                .cancellationRate(calculateRate(cancelledOrders, totalOrders))
                .rejectionRate(calculateRate(rejectedOrders, totalOrders))
                .build();
    }
}
