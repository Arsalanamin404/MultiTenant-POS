package com.arsalan.tenanttable.expenses.dto;

import com.arsalan.tenanttable.payment.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponseDto {

    private UUID id;

    private UUID categoryId;

    private String categoryName;

    private String title;

    private String description;

    private BigDecimal amount;

    private LocalDate expenseDate;

    private PaymentMethod paymentMethod;

    private UUID createdById;

    private String createdByName;

    private Instant createdAt;

    private Instant updatedAt;
}
