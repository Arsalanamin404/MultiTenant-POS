package com.arsalan.tenanttable.staff.dto;

import com.arsalan.tenanttable.common.enums.TenantRole;
import com.arsalan.tenanttable.staff.enums.InvitationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class StaffInvitationResponseDto {
    private UUID id;
    private String fullName;
    private String email;
    private TenantRole tenantRole;
    private InvitationStatus status;
    private Instant expiresAt;
    private Instant createdAt;
}
