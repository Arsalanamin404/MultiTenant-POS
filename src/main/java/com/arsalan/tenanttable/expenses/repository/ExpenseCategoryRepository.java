package com.arsalan.tenanttable.expenses.repository;

import com.arsalan.tenanttable.expenses.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, UUID> {
    List<ExpenseCategory> findAllByTenantIdAndActiveTrueOrderByNameAsc(UUID tenantId);

    Optional<ExpenseCategory> findByIdAndTenantId(UUID categoryId, UUID tenantId);
    
    boolean existsByTenantIdAndNameIgnoreCase(UUID tenantId, String name);
}
