package com.arsalan.tenanttable.user.dto;

import com.arsalan.tenanttable.common.enums.PlatformRole;
import com.arsalan.tenanttable.common.enums.TenantRole;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class UserResponseDto {
    private UUID id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private PlatformRole platformRole;
    private UUID tenantId;
    private String tenantName;
    private TenantRole tenantRole;
    private boolean emailVerified;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
