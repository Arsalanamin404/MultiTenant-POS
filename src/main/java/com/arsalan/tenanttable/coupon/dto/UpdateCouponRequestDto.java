package com.arsalan.tenanttable.coupon.dto;

import com.arsalan.tenanttable.coupon.enums.CouponType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCouponRequestDto {

    @Size(max = 30)
    private String code;

    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    private CouponType couponType;

    @DecimalMin(value = "0.01", message = "Coupon value must be greater than zero")
    private BigDecimal value;

    @PositiveOrZero
    private BigDecimal minimumOrderAmount;

    @PositiveOrZero
    private BigDecimal maximumDiscount;

    @Positive
    private Integer usageLimit;

    private Instant startsAt;

    private Instant expiresAt;

    private Boolean active;

}