package com.arsalan.tenanttable.expenses.repository;

import com.arsalan.tenanttable.expenses.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    Page<Expense> findAllByTenantId(UUID tenantId, Pageable pageable);

    Optional<Expense> findByIdAndTenantId(UUID expenseId, UUID tenantId);

    boolean existsByCategoryId(UUID categoryId);
}
