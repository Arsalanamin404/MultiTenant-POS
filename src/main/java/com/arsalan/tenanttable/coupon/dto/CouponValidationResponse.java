package com.arsalan.tenanttable.coupon.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CouponValidationResponse {

    private boolean valid;

    private String message;

    private BigDecimal discount;

    private BigDecimal finalAmount;

}
