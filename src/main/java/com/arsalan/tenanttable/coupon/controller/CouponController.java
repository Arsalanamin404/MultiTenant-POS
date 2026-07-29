package com.arsalan.tenanttable.coupon.controller;

import com.arsalan.tenanttable.common.dto.ApiResponse;
import com.arsalan.tenanttable.coupon.dto.*;
import com.arsalan.tenanttable.coupon.service.ICouponService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final ICouponService couponService;

    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    @PostMapping
    public ResponseEntity<ApiResponse<CouponResponseDto>> create(
            @Valid @RequestBody CreateCouponRequestDto dto,
            HttpServletRequest request
    ) {
        CouponResponseDto coupon = couponService.create(dto);

        ApiResponse<CouponResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Coupon created",
                coupon,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @PreAuthorize("hasAnyRole('OWNER','MANAGER','CASHIER')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CouponResponseDto>>> getAll(
            @PageableDefault(sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable,
            HttpServletRequest request
    ) {
        Page<CouponResponseDto> coupon = couponService.getAll(pageable);

        ApiResponse<Page<CouponResponseDto>> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Coupons retrieved",
                coupon,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('OWNER','MANAGER','CASHIER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponseDto>> getById(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {
        CouponResponseDto coupon = couponService.getById(id);

        ApiResponse<CouponResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Coupon retrieved",
                coupon,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponseDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCouponRequestDto dto,
            HttpServletRequest request
    ) {
        CouponResponseDto coupon = couponService.update(id, dto);

        ApiResponse<CouponResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Coupon updated",
                coupon,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {
        couponService.delete(id);

        ApiResponse<Void> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Coupon deleted",
                null,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<CouponResponseDto>> activate(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {
        CouponResponseDto coupon = couponService.activate(id);

        ApiResponse<CouponResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Coupon activated",
                coupon,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<CouponResponseDto>> deactivate(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {
        CouponResponseDto coupon = couponService.deactivate(id);

        ApiResponse<CouponResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Coupon deactivated",
                coupon,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('OWNER','MANAGER','CASHIER')")
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<CouponValidationResponse>> validate(
            @Valid @RequestBody ValidateCouponRequestDto dto,
            HttpServletRequest request
    ) {
        CouponValidationResponse coupon = couponService.validate(dto.getCouponCode(), dto.getOrderAmount());

        ApiResponse<CouponValidationResponse> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Coupon validated",
                coupon,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }
}
