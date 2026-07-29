package com.arsalan.tenanttable.coupon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class ValidateCouponRequestDto {

    @NotBlank(message = "Coupon code is required.")
    private String couponCode;

    @NotNull(message = "Order amount is required.")
    @Positive(message = "Order amount must be greater than zero.")
    private BigDecimal orderAmount;
}
