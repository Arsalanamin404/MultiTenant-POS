package com.arsalan.tenanttable.administration.tenant.dto;

import com.arsalan.tenanttable.tenant.enums.PlanType;
import com.arsalan.tenanttable.tenant.enums.TenantStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class TenantSummaryResponseDto {
    UUID id;
    String name;
    String slug;

    TenantStatus tenantStatus;
    PlanType planType;

    String ownerName;
    String ownerEmail;

    Instant createdAt;
}
