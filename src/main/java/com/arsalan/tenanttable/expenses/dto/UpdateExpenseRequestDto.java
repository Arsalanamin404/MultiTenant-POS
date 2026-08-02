package com.arsalan.tenanttable.expenses.dto;

import com.arsalan.tenanttable.payment.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateExpenseRequestDto {

    private UUID categoryId;

    @Size(max = 150)
    private String title;

    @Size(max = 500)
    private String description;

    @DecimalMin(value = "0.01")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

    @PastOrPresent
    private LocalDate expenseDate;

    private PaymentMethod paymentMethod;
}