package com.arsalan.tenanttable.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyCouponRequestDto {
    @NotBlank(message = "coupon code is required")
    @Size(max = 50)
    private String couponCode;
}
