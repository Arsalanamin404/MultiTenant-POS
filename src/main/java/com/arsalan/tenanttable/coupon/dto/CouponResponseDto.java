package com.arsalan.tenanttable.coupon.dto;


import com.arsalan.tenanttable.coupon.enums.CouponType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class CouponResponseDto {

    private UUID id;

    private String code;

    private String name;

    private String description;

    private CouponType type;

    private BigDecimal value;

    private BigDecimal minimumOrderAmount;

    private BigDecimal maximumDiscount;

    private Integer usageLimit;

    private Integer usedCount;

    private Instant startsAt;

    private Instant expiresAt;

    private Boolean active;

    private UUID createdById;
    private String createdByName;
    
    private UUID updatedById;
    private String updatedByName;

    private Instant createdAt;

    private Instant updatedAt;

}
