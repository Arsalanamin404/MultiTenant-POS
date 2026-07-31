package com.arsalan.tenanttable.administration.tenant.mapper;

import com.arsalan.tenanttable.administration.tenant.dto.TenantSummaryProjection;
import com.arsalan.tenanttable.administration.tenant.dto.TenantSummaryResponseDto;

public final class PlatformTenantMapper {
    private PlatformTenantMapper() {
    }

    public static TenantSummaryResponseDto toDto(
            TenantSummaryProjection projection
    ) {
        return TenantSummaryResponseDto.builder()
                .id(projection.id())
                .name(projection.name())
                .tenantStatus(projection.tenantStatus())
                .planType(projection.planType())
                .ownerName(projection.ownerName())
                .ownerEmail(projection.ownerEmail())
                .createdAt(projection.createdAt())
                .build();
    }
}
