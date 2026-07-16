package com.arsalan.tenanttable.auth.dto;

import com.arsalan.tenanttable.common.enums.TenantRole;
import com.arsalan.tenanttable.tenant.enums.PlanType;
import com.arsalan.tenanttable.tenant.enums.TenantStatus;
import com.arsalan.tenanttable.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserResponseDto {
    private UUID id;
    private String fullName;
    private String email;
    private String phoneNumber;

    private UUID tenantId;
    private String tenantName;
    private String tenantAddress;
    private String tenantPhoneNumber;
    private BigDecimal taxRate;

    private TenantRole tenantRole;
    private TenantStatus tenantStatus;
    private PlanType planType;

    private Instant createdAt;
}