package com.arsalan.tenanttable.coupon.mapper;

import com.arsalan.tenanttable.coupon.dto.CouponResponseDto;
import com.arsalan.tenanttable.coupon.dto.CreateCouponRequestDto;
import com.arsalan.tenanttable.coupon.dto.UpdateCouponRequestDto;
import com.arsalan.tenanttable.coupon.entity.Coupon;

public final class CouponMapper {
    private CouponMapper() {
    }

    public static CouponResponseDto toDto(Coupon coupon) {
        return CouponResponseDto.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .name(coupon.getName())
                .description(coupon.getDescription())
                .type(coupon.getCouponType())
                .value(coupon.getValue())
                .minimumOrderAmount(coupon.getMinimumOrderAmount())
                .maximumDiscount(coupon.getMaximumDiscount())
                .usageLimit(coupon.getUsageLimit())
                .usedCount(coupon.getUsedCount())
                .startsAt(coupon.getStartsAt())
                .expiresAt(coupon.getExpiresAt())
                .active(coupon.getActive())
                .createdById(coupon.getCreatedBy().getId())
                .createdByName(coupon.getCreatedBy().getFullName())
                .updatedById(coupon.getUpdatedBy().getId())
                .updatedByName(coupon.getUpdatedBy().getFullName())
                .createdAt(coupon.getCreatedAt())
                .updatedAt(coupon.getUpdatedAt())
                .build();
    }

    public static Coupon toEntity(CreateCouponRequestDto request) {

        return Coupon.builder()
                .code(request.getCode().trim().toUpperCase())
                .name(request.getName().trim())
                .description(request.getDescription())
                .couponType(request.getType())
                .value(request.getValue())
                .minimumOrderAmount(request.getMinimumOrderAmount())
                .maximumDiscount(request.getMaximumDiscount())
                .usageLimit(request.getUsageLimit())
                .usedCount(0)
                .startsAt(request.getStartsAt())
                .expiresAt(request.getExpiresAt())
                .active(request.getActive() == null || request.getActive())
                .build();
    }

    public static void updateEntity(
            Coupon coupon,
            UpdateCouponRequestDto request
    ) {

        coupon.setName(request.getName().trim());
        coupon.setDescription(request.getDescription());
        coupon.setCouponType(request.getCouponType());
        coupon.setValue(request.getValue());
        coupon.setMinimumOrderAmount(request.getMinimumOrderAmount());
        coupon.setMaximumDiscount(request.getMaximumDiscount());
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setStartsAt(request.getStartsAt());
        coupon.setExpiresAt(request.getExpiresAt());
        coupon.setActive(request.getActive());
    }
}
