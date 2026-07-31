package com.arsalan.tenanttable.expenses.service;

import com.arsalan.tenanttable.AuditLog.enums.AuditAction;
import com.arsalan.tenanttable.AuditLog.enums.AuditEntityType;
import com.arsalan.tenanttable.AuditLog.service.IAuditLogService;
import com.arsalan.tenanttable.common.utils.ICurrentUserUtilService;
import com.arsalan.tenanttable.exception.InvalidOperationException;
import com.arsalan.tenanttable.exception.ResourceAlreadyExistsException;
import com.arsalan.tenanttable.exception.ResourceNotFoundException;
import com.arsalan.tenanttable.expenses.dto.CreateExpenseCategoryRequestDto;
import com.arsalan.tenanttable.expenses.dto.ExpenseCategoryResponseDto;
import com.arsalan.tenanttable.expenses.dto.UpdateExpenseCategoryRequestDto;
import com.arsalan.tenanttable.expenses.entity.ExpenseCategory;
import com.arsalan.tenanttable.expenses.mapper.ExpenseCategoryMapper;
import com.arsalan.tenanttable.expenses.repository.ExpenseCategoryRepository;
import com.arsalan.tenanttable.expenses.repository.ExpenseRepository;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.tenant.repository.TenantRepository;
import com.arsalan.tenanttable.user.entity.User;
import com.arsalan.tenanttable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseCategoryService implements IExpenseCategoryService {
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ICurrentUserUtilService currentUserUtilService;
    private final TenantRepository tenantRepository;
    private final ExpenseRepository expenseRepository;
    private final IAuditLogService auditLogService;
    private final UserRepository userRepository;

    private User getOrThrowCurrentUser() {
        UUID userId = currentUserUtilService.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Tenant getOrThrowCurrentTenant() {
        UUID tenantId = currentUserUtilService.getCurrentTenantId();
        return tenantRepository
                .findById(tenantId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("TENANT_NOT_FOUND")
                );
    }

    private ExpenseCategory getOrThrowCategory(UUID categoryId, UUID tenantId) {

        return expenseCategoryRepository
                .findByIdAndTenantId(categoryId, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Expense category not found."));
    }


    @Override
    @Transactional
    public ExpenseCategoryResponseDto createCategory(CreateExpenseCategoryRequestDto dto) {
        Tenant currentTenant = getOrThrowCurrentTenant();
        User currentUser = getOrThrowCurrentUser();

        String name = dto.getName().trim();

        if (expenseCategoryRepository.existsByTenantIdAndNameIgnoreCase(currentTenant.getId(), name)) {
            throw new ResourceAlreadyExistsException("Expense category already exists.");
        }

        ExpenseCategory category = ExpenseCategory.builder()
                .tenant(currentTenant)
                .name(name)
                .description(dto.getDescription())
                .build();

        expenseCategoryRepository.save(category);

        auditLogService.log(
                currentUser,
                AuditAction.CREATE,
                AuditEntityType.EXPENSE_CATEGORY,
                category.getId(),
                "Expense category created"
        );

        log.info("Expense category '{}' created for tenantId={}",
                category.getName(), category.getId());

        return ExpenseCategoryMapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseCategoryResponseDto> getCategories() {
        Tenant currentTenant = getOrThrowCurrentTenant();
        return expenseCategoryRepository
                .findAllByTenantIdAndActiveTrueOrderByNameAsc(currentTenant.getId())
                .stream()
                .map(ExpenseCategoryMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ExpenseCategoryResponseDto updateCategory(
            UUID categoryId,
            UpdateExpenseCategoryRequestDto dto) {

        User currentUser = getOrThrowCurrentUser();
        Tenant currentTenant = getOrThrowCurrentTenant();

        ExpenseCategory category = getOrThrowCategory(categoryId, currentTenant.getId());

        if (dto.getName() != null) {
            String name = dto.getName().trim();

            if (!category.getName().equalsIgnoreCase(name)
                    && expenseCategoryRepository
                    .existsByTenantIdAndNameIgnoreCase(
                            currentTenant.getId(), name)) {

                throw new ResourceAlreadyExistsException("Expense category already exists.");
            }
            category.setName(name);
        }

        if (dto.getDescription() != null) {
            category.setDescription(dto.getDescription());
        }

        if (dto.getActive() != null) {
            category.setActive(dto.getActive());
        }

        auditLogService.log(
                currentUser,
                AuditAction.UPDATE,
                AuditEntityType.EXPENSE_CATEGORY,
                category.getId(),
                "Expense category updated"
        );

        log.info("Expense category '{}' updated.", category.getName());

        return ExpenseCategoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public ExpenseCategoryResponseDto archiveCategory(UUID categoryId) {
        Tenant currentTenant = getOrThrowCurrentTenant();
        User currentUser = getOrThrowCurrentUser();

        ExpenseCategory category = getOrThrowCategory(categoryId, currentTenant.getId());

        if (expenseRepository.existsByCategoryId(categoryId)) {
            throw new InvalidOperationException("Cannot archive a category that has expenses.");
        }

        category.setActive(false);

        auditLogService.log(
                currentUser,
                AuditAction.UPDATE,
                AuditEntityType.EXPENSE_CATEGORY,
                category.getId(),
                "Expense category archived"
        );

        log.info("Expense category '{}' archived for tenant id '{}'.", category.getName(), currentTenant.getId());

        return ExpenseCategoryMapper.toDto(category);
    }
}
