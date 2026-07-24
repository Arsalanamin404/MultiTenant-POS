package com.arsalan.tenanttable.auth.dto;

import com.arsalan.tenanttable.common.enums.PlatformRole;
import com.arsalan.tenanttable.common.enums.TenantRole;
import com.arsalan.tenanttable.tenant.enums.PlanType;
import com.arsalan.tenanttable.tenant.enums.TenantStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class RegisterResponseDto {
    private UUID id;
    private String fullName;
    private String email;
    private String phoneNumber;

    private UUID tenantId;
    private String tenantName;
    private String tenantAddress;
    private String tenantPhoneNumber;
    private BigDecimal taxRate;

    private PlatformRole platformRole;
    private TenantRole tenantRole;
    private TenantStatus tenantStatus;
    private PlanType planType;

    private Instant createdAt;
}