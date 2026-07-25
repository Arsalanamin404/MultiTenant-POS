package com.arsalan.tenanttable.common.seeds;

import com.arsalan.tenanttable.expenses.entity.ExpenseCategory;
import com.arsalan.tenanttable.tenant.entity.Tenant;

import java.util.List;

public final class ExpenseCategorySeeder {
    private static final List<String> DEFAULT_EXPENSE_CATEGORIES = List.of(
            "Rent",
            "Salary",
            "Electricity",
            "Water",
            "Gas",
            "Internet",
            "Maintenance",
            "Cleaning",
            "Office Supplies",
            "Marketing",
            "Tax",
            "Other"
    );

    private ExpenseCategorySeeder() {
    }

    private static ExpenseCategory category(Tenant tenant, String name) {
        return ExpenseCategory.builder()
                .tenant(tenant)
                .name(name)
                .active(true)
                .build();
    }

    public static List<ExpenseCategory> createDefaultCategories(Tenant tenant) {
        return DEFAULT_EXPENSE_CATEGORIES
                .stream()
                .map(name -> category(tenant, name))
                .toList();
    }
}
