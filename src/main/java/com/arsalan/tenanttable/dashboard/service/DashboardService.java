package com.arsalan.tenanttable.dashboard.service;

import com.arsalan.tenanttable.common.utils.DateRangeUtil;
import com.arsalan.tenanttable.common.utils.ICurrentUserUtilService;
import com.arsalan.tenanttable.dashboard.dto.DashboardResponseDto;
import com.arsalan.tenanttable.dining_table.enums.DiningTableStatus;
import com.arsalan.tenanttable.dining_table.repository.DiningTableRepository;
import com.arsalan.tenanttable.expenses.repository.ExpenseRepository;
import com.arsalan.tenanttable.order.enums.OrderStatus;
import com.arsalan.tenanttable.order.repository.OrderRepository;
import com.arsalan.tenanttable.payment.repository.PaymentRepository;
import com.arsalan.tenanttable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DashboardService implements IDashboardService {
    private final PaymentRepository paymentRepository;
    private final ExpenseRepository expenseRepository;
    private final OrderRepository orderRepository;
    private final DiningTableRepository diningTableRepository;
    private final UserRepository userRepository;
    private final ICurrentUserUtilService currentUserUtilService;


    @Override
    public DashboardResponseDto getDashboard() {
        UUID tenantId = currentUserUtilService.getCurrentTenantId();
        log.debug("Loading dashboard for tenant {}", tenantId);

        // DATES
        LocalDate today = DateRangeUtil.today();

        Instant startOfToday = DateRangeUtil.startOfToday();
        Instant startOfTomorrow = DateRangeUtil.startOfTomorrow();

        LocalDate firstDayOfMonth = DateRangeUtil.firstDayOfCurrentMonth();
        LocalDate firstDayOfNextMonth = DateRangeUtil.firstDayOfNextMonth();

        Instant startOfMonth = DateRangeUtil.startOfCurrentMonth();
        Instant startOfNextMonth = DateRangeUtil.startOfNextMonth();


        // Revenue
        BigDecimal overallRevenue = paymentRepository.sumOverallRevenue(tenantId);
        BigDecimal todayRevenue = paymentRepository.sumRevenueBetween(
                tenantId,
                startOfToday,
                startOfTomorrow
        );
        BigDecimal monthlyRevenue = paymentRepository.sumRevenueBetween(
                tenantId,
                startOfMonth,
                startOfNextMonth
        );

        // Expense
        BigDecimal overallExpenses = expenseRepository.getOverallExpenses(tenantId);
        BigDecimal todayExpenses = expenseRepository.getExpensesForDate(tenantId, today);
        BigDecimal monthlyExpenses = expenseRepository.getExpensesBetween(
                tenantId,
                firstDayOfMonth,
                firstDayOfNextMonth
        );

        // Profit
        BigDecimal overallProfit = overallRevenue.subtract(overallExpenses);
        BigDecimal todayProfit = todayRevenue.subtract(todayExpenses);
        BigDecimal monthlyProfit = monthlyRevenue.subtract(monthlyExpenses);

        // Orders
        long totalOrders = orderRepository.countByTenantId(tenantId);
        long todayOrders = orderRepository.countOrdersBetween(
                tenantId,
                startOfToday,
                startOfTomorrow
        );
        long pendingOrders = orderRepository.countByTenantIdAndStatus(tenantId, OrderStatus.PENDING);
        long completedOrders = orderRepository.countByTenantIdAndStatus(tenantId, OrderStatus.COMPLETED);
        long cancelledOrders = orderRepository.countByTenantIdAndStatus(tenantId, OrderStatus.CANCELLED);

        // Tables
        long totalTables = diningTableRepository.countByTenantId(tenantId);
        long occupiedTables = diningTableRepository.countByTenantIdAndStatus(tenantId, DiningTableStatus.OCCUPIED);
        long availableTables = diningTableRepository.countByTenantIdAndStatus(tenantId, DiningTableStatus.AVAILABLE);

        // Staff
        long totalStaff = userRepository.countByTenantId(tenantId);
        long activeStaff = userRepository.countByTenantIdAndActiveTrue(tenantId);

        return DashboardResponseDto.builder()
                .overallRevenue(overallRevenue)
                .todayRevenue(todayRevenue)
                .monthlyRevenue(monthlyRevenue)

                .totalOrders(totalOrders)
                .todayOrders(todayOrders)
                .pendingOrders(pendingOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)

                .todayExpenses(todayExpenses)
                .monthlyExpenses(monthlyExpenses)
                .overallExpenses(overallExpenses)

                .overallProfit(overallProfit)
                .todayProfit(todayProfit)
                .monthlyProfit(monthlyProfit)

                .occupiedTables(occupiedTables)
                .availableTables(availableTables)
                .totalTables(totalTables)

                .totalStaff(totalStaff)
                .activeStaff(activeStaff)

                .build();
    }
}
