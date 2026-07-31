package com.arsalan.tenanttable.administration.tenant.service;

import com.arsalan.tenanttable.AuditLog.enums.AuditAction;
import com.arsalan.tenanttable.AuditLog.enums.AuditEntityType;
import com.arsalan.tenanttable.AuditLog.service.IAuditLogService;
import com.arsalan.tenanttable.administration.tenant.dto.*;
import com.arsalan.tenanttable.administration.tenant.mapper.PlatformTenantMapper;
import com.arsalan.tenanttable.category.repository.CategoryRepository;
import com.arsalan.tenanttable.common.enums.TenantRole;
import com.arsalan.tenanttable.common.utils.ICurrentUserUtilService;
import com.arsalan.tenanttable.dining_table.repository.DiningTableRepository;
import com.arsalan.tenanttable.exception.InvalidOperationException;
import com.arsalan.tenanttable.exception.ResourceNotFoundException;
import com.arsalan.tenanttable.menu.repository.MenuRepository;
import com.arsalan.tenanttable.order.repository.OrderRepository;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.tenant.enums.PlanType;
import com.arsalan.tenanttable.tenant.enums.TenantStatus;
import com.arsalan.tenanttable.tenant.repository.TenantRepository;
import com.arsalan.tenanttable.user.entity.User;
import com.arsalan.tenanttable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlatformTenantServiceImpl implements IPlatformTenantService {
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final MenuRepository menuRepository;
    private final DiningTableRepository diningTableRepository;
    private final OrderRepository orderRepository;
    private final IAuditLogService auditLogService;
    private final ICurrentUserUtilService currentUserUtilService;

    private Tenant getOrThrowTenantById(UUID tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("tenantId:" + tenantId)
                );
    }

    private User getTenantOwner(UUID tenantId) {
        return userRepository.findByTenantIdAndTenantRole(
                tenantId,
                TenantRole.OWNER
        ).orElseThrow(() -> new ResourceNotFoundException("Tenant owner not found. tenantId: " + tenantId));
    }

    private User getOrThrowCurrentUser() {
        UUID userId = currentUserUtilService.getCurrentUserId();

        return userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not authenticated"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TenantSummaryResponseDto> getTenants(String search, TenantStatus status, Pageable pageable) {
        log.info("Fetching tenants. search={}, status={}", search, status);
        Page<TenantSummaryProjection> result = tenantRepository.findAllForPlatform(
                search,
                status,
                pageable
        );

        log.info("Found {} tenants.", result.getTotalElements());
        return result.map(PlatformTenantMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public TenantDetailsResponseDto getTenantDetails(UUID tenantId) {
        log.info("Fetching tenant details for tenantId={}", tenantId);

        Tenant tenant = getOrThrowTenantById(tenantId);
        User owner = getTenantOwner(tenantId);

        long totalUsers = userRepository.countByTenantId(tenantId);
        long totalCategories = categoryRepository.countByTenantId(tenantId);
        long totalMenuItems = menuRepository.countByTenantId(tenantId);
        long totalDiningTables = diningTableRepository.countByTenantId(tenantId);
        long totalOrders = orderRepository.countByTenantId(tenantId);

        log.info("Tenant details loaded successfully. tenantId={}", tenantId);

        return TenantDetailsResponseDto.builder()
                .id(tenantId)
                .name(tenant.getName())
                .tenantStatus(tenant.getTenantStatus())
                .planType(tenant.getPlanType())
                .createdAt(tenant.getCreatedAt())
                .updatedAt(tenant.getUpdatedAt())
                .trialEndsAt(tenant.getTrialEndsAt())
                .suspendedAt(tenant.getSuspendedAt())
                .owner(
                        OwnerSummaryDto.builder()
                                .id(owner.getId())
                                .fullName(owner.getFullName())
                                .email(owner.getEmail())
                                .phoneNumber(owner.getPhoneNumber())
                                .build()
                )
                .statistics(
                        BusinessStatisticsDto.builder()
                                .totalUsers(totalUsers)
                                .totalCategories(totalCategories)
                                .totalMenuItems(totalMenuItems)
                                .totalDiningTables(totalDiningTables)
                                .totalOrders(totalOrders)
                                .build()
                )
                .build();
    }

    @Override
    public void updateTenantStatus(UUID tenantId, UpdateTenantStatusRequestDto request) {
        Tenant tenant = getOrThrowTenantById(tenantId);
        User currentUser = getOrThrowCurrentUser();

        if (tenant.getTenantStatus() == request.tenantStatus()) {
            throw new InvalidOperationException("Tenant status is already " + request.tenantStatus());
        }

        String previousStatus = tenant.getTenantStatus().toString();

        tenant.setTenantStatus(request.tenantStatus());

        if (request.tenantStatus() == TenantStatus.SUSPENDED) {
            tenant.setSuspendedAt(LocalDateTime.now());
        } else {
            tenant.setSuspendedAt(null);
        }

        Tenant savedTenant = tenantRepository.save(tenant);

        auditLogService.log(
                currentUser,
                AuditAction.UPDATE,
                AuditEntityType.TENANT,
                savedTenant.getId(),
                "Tenant status updated by SUPER_ADMIN"
        );

        log.info(
                "SUPER_ADMIN {} updated tenant {} status from {} to {}",
                currentUser.getId(),
                tenant.getId(),
                previousStatus,
                request.tenantStatus()
        );
    }

    @Override
    @Transactional
    public void updateTenantPlan(UUID tenantId, UpdateTenantPlanRequestDto request) {
        log.info("Updating plan for tenantId={}", tenantId);

        Tenant tenant = getOrThrowTenantById(tenantId);
        User currentUser = getOrThrowCurrentUser();

        PlanType previousPlan = tenant.getPlanType();

        if (previousPlan == request.planType()) {
            throw new InvalidOperationException(
                    "Tenant is already on the " + previousPlan + " plan."
            );
        }

        tenant.setPlanType(request.planType());

        tenantRepository.save(tenant);

        auditLogService.log(
                currentUser,
                AuditAction.UPDATE,
                AuditEntityType.TENANT,
                tenant.getId(),
                String.format(
                        "Tenant '%s' plan changed from %s to %s",
                        tenant.getName(),
                        previousPlan,
                        request.planType()
                )
        );

        log.info(
                "Tenant plan updated successfully. tenantId={}, oldPlan={}, newPlan={}",
                tenant.getId(),
                previousPlan,
                request.planType()
        );
    }
}
