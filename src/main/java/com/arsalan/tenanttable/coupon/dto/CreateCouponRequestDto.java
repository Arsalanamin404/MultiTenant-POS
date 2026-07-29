package com.arsalan.tenanttable.coupon.dto;

import com.arsalan.tenanttable.coupon.enums.CouponType;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class CreateCouponRequestDto {

    @NotBlank(message = "Coupon code is required")
    @Size(max = 30)
    private String code;

    @NotBlank(message = "Coupon name is required")
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull(message = "Coupon type is required")
    private CouponType type;

    @NotNull(message = "Coupon value is required")
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