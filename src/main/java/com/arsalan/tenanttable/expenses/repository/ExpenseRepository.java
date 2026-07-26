package com.arsalan.tenanttable.expenses.repository;

import com.arsalan.tenanttable.expenses.entity.Expense;
import com.arsalan.tenanttable.reports.dto.ExpenseCategorySummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    Page<Expense> findAllByTenantId(UUID tenantId, Pageable pageable);

    Optional<Expense> findByIdAndTenantId(UUID expenseId, UUID tenantId);

    boolean existsByCategoryId(UUID categoryId);

    @Query("""
            SELECT COALESCE(SUM(e.amount), 0.00)
            FROM Expense e
            WHERE e.tenant.id = :tenantId
            """)
    BigDecimal getOverallExpenses(@Param("tenantId") UUID tenantId);

    @Query("""
            SELECT COALESCE(SUM(e.amount), 0.00)
            FROM Expense e
            WHERE e.tenant.id = :tenantId
            AND e.expenseDate = :date
            """)
    BigDecimal getExpensesForDate(@Param("tenantId") UUID tenantId, @Param("date") LocalDate date);

    @Query("""
            SELECT COALESCE(SUM(e.amount), 0.00)
            FROM Expense e
            WHERE e.tenant.id = :tenantId
            AND e.expenseDate >= :startDate
            AND e.expenseDate < :endDate
            """)
    BigDecimal getExpensesBetween(
            @Param("tenantId") UUID tenantId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT new com.arsalan.tenanttable.reports.dto.ExpenseCategorySummaryDto(
                e.category.id,
                e.category.name,
                COALESCE(SUM(e.amount), 0)
            )
            FROM Expense e
            WHERE e.tenant.id = :tenantId
            AND e.expenseDate >= :startDate
            AND e.expenseDate < :endDate
            GROUP BY
                e.category.id,
                e.category.name
            ORDER BY
                SUM(e.amount) DESC
            """)
    List<ExpenseCategorySummaryDto> getExpenseCategorySummary(
            UUID tenantId,
            LocalDate startDate,
            LocalDate endDate
    );
}
