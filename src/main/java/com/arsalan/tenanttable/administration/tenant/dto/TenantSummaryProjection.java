package com.arsalan.tenanttable.administration.tenant.dto;

import com.arsalan.tenanttable.tenant.enums.PlanType;
import com.arsalan.tenanttable.tenant.enums.TenantStatus;

import java.time.Instant;
import java.util.UUID;

public record TenantSummaryProjection(
        UUID id,
        String name,
        TenantStatus tenantStatus,
        PlanType planType,

        String ownerName,
        String ownerEmail,

        Instant createdAt

) {
}