package com.arsalan.tenanttable.administration.tenant.dto;

import com.arsalan.tenanttable.tenant.enums.PlanType;
import com.arsalan.tenanttable.tenant.enums.TenantStatus;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record TenantDetailsResponseDto(

        UUID id,
        String name,

        TenantStatus tenantStatus,
        PlanType planType,

        Instant createdAt,
        Instant updatedAt,

        LocalDateTime trialEndsAt,
        LocalDateTime suspendedAt,

        OwnerSummaryDto owner,

        BusinessStatisticsDto statistics
) {
}
