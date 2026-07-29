package com.arsalan.tenanttable.coupon.service;

import com.arsalan.tenanttable.AuditLog.enums.AuditAction;
import com.arsalan.tenanttable.AuditLog.enums.AuditEntityType;
import com.arsalan.tenanttable.AuditLog.service.IAuditLogService;
import com.arsalan.tenanttable.common.utils.ICurrentUserUtilService;
import com.arsalan.tenanttable.coupon.dto.CouponResponseDto;
import com.arsalan.tenanttable.coupon.dto.CouponValidationResponse;
import com.arsalan.tenanttable.coupon.dto.CreateCouponRequestDto;
import com.arsalan.tenanttable.coupon.dto.UpdateCouponRequestDto;
import com.arsalan.tenanttable.coupon.entity.Coupon;
import com.arsalan.tenanttable.coupon.enums.CouponType;
import com.arsalan.tenanttable.coupon.mapper.CouponMapper;
import com.arsalan.tenanttable.coupon.repository.CouponRepository;
import com.arsalan.tenanttable.exception.InvalidOperationException;
import com.arsalan.tenanttable.exception.ResourceAlreadyExistsException;
import com.arsalan.tenanttable.exception.ResourceNotFoundException;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.tenant.repository.TenantRepository;
import com.arsalan.tenanttable.user.entity.User;
import com.arsalan.tenanttable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponService implements ICouponService {
    private final ICurrentUserUtilService currentUserUtilService;
    private final CouponRepository couponRepository;
    private final IAuditLogService auditLogService;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;

    private Tenant getOrThrowCurrentTenant() {
        UUID tenantId = currentUserUtilService.getCurrentTenantId();

        return tenantRepository
                .findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("TENANT_NOT_FOUND"));
    }

    private User getOrThrowCurrentUser() {
        UUID userId = currentUserUtilService.getCurrentUserId();

        return userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND"));
    }

    private Coupon getCouponOrThrow(UUID id) {
        Tenant tenant = getOrThrowCurrentTenant();
        return couponRepository
                .findByIdAndTenant(id, tenant)
                .orElseThrow(() -> new ResourceNotFoundException("COUPON_NOT_FOUND"));
    }

    private Coupon getCouponByCodeOrThrow(String code) {
        Tenant tenant = getOrThrowCurrentTenant();
        String normalizedCouponCode = code.trim().toUpperCase();

        return couponRepository
                .findByCodeAndTenant(normalizedCouponCode, tenant)
                .orElseThrow(() -> new ResourceNotFoundException("COUPON_NOT_FOUND"));
    }

    private void ensureCouponCodeUnique(String code, Tenant tenant) {
        if (couponRepository.existsByCodeAndTenant(code, tenant)) {
            throw new ResourceAlreadyExistsException("Coupon with this code already exists.");
        }
    }

    private void validateDateRange(Instant startsAt, Instant expiresAt) {
        if (startsAt != null &&
                expiresAt != null &&
                startsAt.isAfter(expiresAt)) {
            throw new InvalidOperationException("Start date cannot be after expiry date.");
        }
    }

    private BigDecimal calculateDiscount(Coupon coupon, BigDecimal orderAmount) {
        BigDecimal discount;
        if (coupon.getCouponType().equals(CouponType.PERCENTAGE)) {
            discount = orderAmount
                    .multiply(coupon.getValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (coupon.getMaximumDiscount() != null &&
                    discount.compareTo(coupon.getMaximumDiscount()) > 0) {
                return coupon.getMaximumDiscount();
            }
            return discount;
        } else {
            discount = coupon.getValue();
            if (discount.compareTo(orderAmount) > 0) {
                return orderAmount;
            }
        }
        return discount;
    }

    private void validateCoupon(Coupon coupon, BigDecimal orderAmount) {
        if (!coupon.getActive()) {
            log.debug("Coupon is inactive. couponCode={}", coupon.getCode());
            throw new InvalidOperationException("Coupon is inactive.");
        }

        Instant now = Instant.now();

        if (coupon.getStartsAt() != null
                && coupon.getStartsAt().isAfter(now)) {
            log.debug("Coupon is not active yet couponCode={}", coupon.getCode());
            throw new InvalidOperationException("Coupon is not active yet.");
        }


        if (coupon.getExpiresAt() != null
                && coupon.getExpiresAt().isBefore(now)) {
            log.debug("Coupon is expired couponCode={}", coupon.getCode());
            throw new InvalidOperationException("Coupon is expired.");
        }

        if (coupon.getUsedCount() >= coupon.getUsageLimit()) {
            log.debug("Coupon usage limit reached couponCode={}", coupon.getCode());
            throw new InvalidOperationException("Coupon usage limit reached.");
        }

        if (orderAmount.compareTo(coupon.getMinimumOrderAmount()) < 0) {
            log.debug(
                    "Minimum order amount not met: orderAmount={}, minimumOrderAmountRequired={}, couponCode={}",
                    orderAmount,
                    coupon.getMinimumOrderAmount(),
                    coupon.getCode()
            );
            throw new InvalidOperationException(
                    "Minimum order amount not met. MINIMUM_ORDER_AMOUNT required: " + coupon.getMinimumOrderAmount()
            );
        }
    }

    @Override
    @Transactional
    public CouponResponseDto create(CreateCouponRequestDto dto) {
        Tenant currentTenant = getOrThrowCurrentTenant();
        User currentUser = getOrThrowCurrentUser();
        String couponCode = dto.getCode().trim().toUpperCase();

        ensureCouponCodeUnique(couponCode, currentTenant);

        validateDateRange(dto.getStartsAt(), dto.getExpiresAt());

        Coupon coupon = CouponMapper.toEntity(dto);

        coupon.setTenant(currentTenant);
        coupon.setCreatedBy(currentUser);

        Coupon savedCoupon = couponRepository.save(coupon);

        auditLogService.log(
                currentUser,
                AuditAction.CREATE,
                AuditEntityType.COUPON,
                savedCoupon.getId(),
                "Coupon created"
        );

        log.debug("Coupon '{}' created by {}", savedCoupon.getCode(), currentUser.getId());

        return CouponMapper.toDto(savedCoupon);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponseDto getById(UUID id) {
        Coupon coupon = getCouponOrThrow(id);
        log.debug("Coupon {} retrieved", coupon.getCode());
        return CouponMapper.toDto(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CouponResponseDto> getAll(Pageable pageable) {
        Tenant currentTenant = getOrThrowCurrentTenant();
        return couponRepository
                .findAllByTenant(currentTenant, pageable)
                .map(CouponMapper::toDto);
    }

    @Override
    @Transactional
    public CouponResponseDto update(UUID id, UpdateCouponRequestDto dto) {
        User currentUser = getOrThrowCurrentUser();
        Coupon coupon = getCouponOrThrow(id);

        validateDateRange(dto.getStartsAt(), dto.getExpiresAt());

        CouponMapper.updateEntity(coupon, dto);
        coupon.setUpdatedBy(currentUser);

        Coupon savedCoupon = couponRepository.save(coupon);

        auditLogService.log(
                currentUser,
                AuditAction.UPDATE,
                AuditEntityType.COUPON,
                savedCoupon.getId(),
                "Coupon updated"
        );

        log.debug("Coupon '{}' updated by user {}", savedCoupon.getCode(), currentUser.getId());

        return CouponMapper.toDto(savedCoupon);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        User currentUser = getOrThrowCurrentUser();
        Coupon coupon = getCouponOrThrow(id);

        couponRepository.delete(coupon);
        auditLogService.log(
                currentUser,
                AuditAction.DELETE,
                AuditEntityType.COUPON,
                coupon.getId(),
                "Coupon deleted"
        );
        log.debug("Coupon deleted code={}", coupon.getCode());
    }

    @Override
    @Transactional(readOnly = true)
    public CouponValidationResponse validate(String couponCode, BigDecimal orderAmount) {
        Coupon coupon = getCouponByCodeOrThrow(couponCode);

        validateCoupon(coupon, orderAmount);

        BigDecimal discount = calculateDiscount(coupon, orderAmount);

        BigDecimal finalAmount = orderAmount.subtract(discount);

        log.debug("Coupon validated code={} discount={} finalAmount={}.",
                couponCode,
                discount,
                finalAmount
        );
        return CouponValidationResponse.builder()
                .valid(true)
                .message("Coupon validated")
                .discount(discount)
                .finalAmount(finalAmount)
                .build();
    }

    @Override
    @Transactional
    public CouponResponseDto activate(UUID id) {
        Coupon coupon = getCouponOrThrow(id);

        if (Boolean.TRUE.equals(coupon.getActive())) {
            return CouponMapper.toDto(coupon);
        }

        User currentUser = getOrThrowCurrentUser();

        coupon.setActive(true);
        coupon.setUpdatedBy(currentUser);

        Coupon savedCoupon = couponRepository.save(coupon);

        auditLogService.log(
                currentUser,
                AuditAction.UPDATE,
                AuditEntityType.COUPON,
                savedCoupon.getId(),
                "Coupon activated"
        );

        log.debug("Coupon '{}' activated by user {}", savedCoupon.getCode(), currentUser.getId());

        return CouponMapper.toDto(savedCoupon);
    }

    @Override
    @Transactional
    public CouponResponseDto deactivate(UUID id) {
        Coupon coupon = getCouponOrThrow(id);

        if (Boolean.FALSE.equals(coupon.getActive())) {
            return CouponMapper.toDto(coupon);
        }

        User currentUser = getOrThrowCurrentUser();

        coupon.setActive(false);
        coupon.setUpdatedBy(currentUser);

        Coupon savedCoupon = couponRepository.save(coupon);

        auditLogService.log(
                currentUser,
                AuditAction.UPDATE,
                AuditEntityType.COUPON,
                savedCoupon.getId(),
                "Coupon deactivated"
        );

        log.debug("Coupon '{}' deactivated by user {}", savedCoupon.getCode(), currentUser.getId());

        return CouponMapper.toDto(savedCoupon);
    }
}
