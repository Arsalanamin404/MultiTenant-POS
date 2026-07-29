package com.arsalan.tenanttable.coupon.service;

import com.arsalan.tenanttable.coupon.dto.CouponResponseDto;
import com.arsalan.tenanttable.coupon.dto.CouponValidationResponse;
import com.arsalan.tenanttable.coupon.dto.CreateCouponRequestDto;
import com.arsalan.tenanttable.coupon.dto.UpdateCouponRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface ICouponService {
    CouponResponseDto create(CreateCouponRequestDto dto);

    CouponResponseDto getById(UUID id);

    Page<CouponResponseDto> getAll(Pageable pageable);

    CouponResponseDto update(UUID id, UpdateCouponRequestDto dto);

    void delete(UUID id);

    CouponValidationResponse validate(String couponCode, BigDecimal orderAmount);

    CouponResponseDto activate(UUID id);

    CouponResponseDto deactivate(UUID id);
}
