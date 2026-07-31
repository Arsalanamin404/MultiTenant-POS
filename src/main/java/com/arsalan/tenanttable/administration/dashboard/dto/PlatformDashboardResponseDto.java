package com.arsalan.tenanttable.administration.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PlatformDashboardResponseDto {
    private long totalTenants;
    private long activeTenants;
    private long suspendedTenants;
    private long trialTenants;
    private long totalUsers;
    private long activeUsers;
    private long verifiedUsers;
}
