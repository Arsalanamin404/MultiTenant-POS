package com.arsalan.tenanttable.administration.tenant.service;

import com.arsalan.tenanttable.administration.tenant.dto.TenantDetailsResponseDto;
import com.arsalan.tenanttable.administration.tenant.dto.TenantSummaryResponseDto;
import com.arsalan.tenanttable.administration.tenant.dto.UpdateTenantPlanRequestDto;
import com.arsalan.tenanttable.administration.tenant.dto.UpdateTenantStatusRequestDto;
import com.arsalan.tenanttable.tenant.enums.TenantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IPlatformTenantService {
    Page<TenantSummaryResponseDto> getTenants(
            String search,
            TenantStatus status,
            Pageable pageable
    );

    TenantDetailsResponseDto getTenantDetails(UUID tenantId);

    void updateTenantStatus(UUID tenantId, UpdateTenantStatusRequestDto request);

    void updateTenantPlan(UUID tenantId, UpdateTenantPlanRequestDto request);
}
