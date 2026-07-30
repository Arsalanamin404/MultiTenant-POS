package com.arsalan.tenanttable.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApplyCouponRequestDto {
    @NotBlank(message = "coupon code is required")
    @Size(max = 50)
    private String couponCode;
}
