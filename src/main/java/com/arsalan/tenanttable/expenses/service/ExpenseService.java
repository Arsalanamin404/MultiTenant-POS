package com.arsalan.tenanttable.expenses.service;

import com.arsalan.tenanttable.common.utils.ICurrentUserUtilService;
import com.arsalan.tenanttable.exception.InvalidOperationException;
import com.arsalan.tenanttable.exception.ResourceNotFoundException;
import com.arsalan.tenanttable.expenses.dto.CreateExpenseRequestDto;
import com.arsalan.tenanttable.expenses.dto.ExpenseResponseDto;
import com.arsalan.tenanttable.expenses.dto.UpdateExpenseRequestDto;
import com.arsalan.tenanttable.expenses.entity.Expense;
import com.arsalan.tenanttable.expenses.entity.ExpenseCategory;
import com.arsalan.tenanttable.expenses.mapper.ExpenseMapper;
import com.arsalan.tenanttable.expenses.repository.ExpenseCategoryRepository;
import com.arsalan.tenanttable.expenses.repository.ExpenseRepository;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.tenant.repository.TenantRepository;
import com.arsalan.tenanttable.user.entity.User;
import com.arsalan.tenanttable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseService implements IExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ICurrentUserUtilService currentUserUtilService;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;

    private User getOrThrowCurrentUser() {
        UUID userId = currentUserUtilService.getCurrentUserId();

        return userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND with user_id: " + userId));
    }

    private Tenant getOrThrowCurrentTenant() {
        UUID tenantId = currentUserUtilService.getCurrentTenantId();

        return tenantRepository
                .findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("TENANT_NOT_FOUND with tenant_id: " + tenantId));
    }

    private Expense getOrThrowExpense(UUID expenseId, UUID tenantId) {
        return expenseRepository
                .findByIdAndTenantId(expenseId, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Expense not found."));
    }

    private ExpenseCategory getOrThrowExpenseCategory(UUID categoryId, UUID tenantId) {
        ExpenseCategory category = expenseCategoryRepository
                .findByIdAndTenantId(categoryId, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Expense category not found."));

        if (!category.isActive()) {
            throw new InvalidOperationException("Expense category is archived.");
        }

        return category;
    }

    @Override
    @Transactional
    public ExpenseResponseDto createExpense(CreateExpenseRequestDto dto) {
        User currentUser = getOrThrowCurrentUser();
        Tenant currentTenant = getOrThrowCurrentTenant();

        ExpenseCategory expenseCategory = getOrThrowExpenseCategory(
                dto.getCategoryId(),
                currentTenant.getId()
        );

        Expense expense = Expense.builder()
                .tenant(currentTenant)
                .category(expenseCategory)
                .title(dto.getTitle().trim())
                .description(dto.getDescription())
                .amount(dto.getAmount())
                .expenseDate(dto.getExpenseDate())
                .paymentMethod(dto.getPaymentMethod())
                .createdBy(currentUser)
                .build();

        expenseRepository.save(expense);

        log.info("Expense '{}' created for tenantId={}",
                expense.getTitle(),
                expense.getTenant().getId());

        return ExpenseMapper.toDto(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseResponseDto> getExpenses(Pageable pageable) {
        Tenant currentTenant = getOrThrowCurrentTenant();

        log.info("Fetching expenses for tenantId={}", currentTenant.getId());

        return expenseRepository
                .findAllByTenantId(currentTenant.getId(), pageable)
                .map(ExpenseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseResponseDto getExpense(UUID expenseId) {
        Tenant currentTenant = getOrThrowCurrentTenant();

        Expense expense = getOrThrowExpense(expenseId, currentTenant.getId());

        return ExpenseMapper.toDto(expense);
    }

    @Override
    @Transactional
    public ExpenseResponseDto updateExpense(UUID expenseId, UpdateExpenseRequestDto dto) {
        User currentUser = getOrThrowCurrentUser();
        Tenant currentTenant = getOrThrowCurrentTenant();
        Expense expense = getOrThrowExpense(expenseId, currentTenant.getId());
        ExpenseCategory expenseCategory = dto.getCategoryId() != null
                ? getOrThrowExpenseCategory(dto.getCategoryId(), currentTenant.getId())
                : null;

        expense.update(dto, expenseCategory);
        expense.setUpdatedBy(currentUser);

        log.info("Expense updated with id={}", expenseId);

        return ExpenseMapper.toDto(expense);
    }

    @Override
    @Transactional
    public ExpenseResponseDto deleteExpense(UUID expenseId) {
        Tenant currentTenant = getOrThrowCurrentTenant();
        Expense expense = getOrThrowExpense(expenseId, currentTenant.getId());

        expenseRepository.delete(expense);
        log.info("Expense deleted with id={}", expenseId);
        return ExpenseMapper.toDto(expense);
    }
}
