package com.arsalan.tenanttable.order.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApplyDiscountRequestDto {
    @DecimalMin("0.00")
    @DecimalMax("100.00")
    private BigDecimal discountRate;
}
