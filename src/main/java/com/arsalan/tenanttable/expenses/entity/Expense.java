package com.arsalan.tenanttable.expenses.entity;

import com.arsalan.tenanttable.expenses.dto.UpdateExpenseRequestDto;
import com.arsalan.tenanttable.payment.enums.PaymentMethod;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "expenses",
        indexes = {
                @Index(name = "idx_category_id", columnList = "category_id"),
                @Index(name = "idx_category_date", columnList = "expense_date")
        }
)
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private ExpenseCategory category;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate expenseDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod = PaymentMethod.CASH;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_id")
    private User updatedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public void update(UpdateExpenseRequestDto dto, ExpenseCategory category) {
        if (dto.getCategoryId() != null) {
            this.category = category;
        }

        if (dto.getTitle() != null) {
            this.title = dto.getTitle();
        }

        if (dto.getDescription() != null) {
            this.description = dto.getDescription();
        }

        if (dto.getAmount() != null) {
            this.amount = dto.getAmount();
        }

        if (dto.getExpenseDate() != null) {
            this.expenseDate = dto.getExpenseDate();
        }

        if (dto.getPaymentMethod() != null) {
            this.paymentMethod = dto.getPaymentMethod();
        }

    }
}
