package com.arsalan.tenanttable.user.dto;

import com.arsalan.tenanttable.common.enums.PlatformRole;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class UserSummaryResponseDto {
    private UUID id;
    private String fullName;
    private String email;
    private PlatformRole platformRole;
    private UUID tenantId;
    private String tenantName;
    private boolean active;
    private boolean emailVerified;
    private Instant createdAt;
}
