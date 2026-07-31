package com.arsalan.tenanttable.administration.dashboard.service;

import com.arsalan.tenanttable.administration.dashboard.dto.PlatformDashboardResponseDto;
import com.arsalan.tenanttable.tenant.enums.TenantStatus;
import com.arsalan.tenanttable.tenant.repository.TenantRepository;
import com.arsalan.tenanttable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlatformDashboardServiceImpl implements IPlatformDashboardService {
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public PlatformDashboardResponseDto getDashboard() {
        log.info("Generating platform dashboard for super admin.");
        long totalTenants = tenantRepository.count();
        long activeTenants = tenantRepository.countByTenantStatus(TenantStatus.ACTIVE);
        long suspendedTenants = tenantRepository.countByTenantStatus(TenantStatus.SUSPENDED);
        long trialTenants = tenantRepository.countByTenantStatus(TenantStatus.TRIAL);

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByActiveTrue();
        long verifiedUsers = userRepository.countByEmailVerifiedTrue();

        return PlatformDashboardResponseDto.builder()
                .totalTenants(totalTenants)
                .activeTenants(activeTenants)
                .suspendedTenants(suspendedTenants)
                .trialTenants(trialTenants)
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .verifiedUsers(verifiedUsers)
                .build();
    }
}
