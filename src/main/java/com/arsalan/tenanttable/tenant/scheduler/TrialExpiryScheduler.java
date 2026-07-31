package com.arsalan.tenanttable.tenant.scheduler;

import com.arsalan.tenanttable.AuditLog.enums.AuditAction;
import com.arsalan.tenanttable.AuditLog.enums.AuditEntityType;
import com.arsalan.tenanttable.AuditLog.service.IAuditLogService;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.tenant.enums.TenantStatus;
import com.arsalan.tenanttable.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrialExpiryScheduler {

    private final TenantRepository tenantRepository;
    private final IAuditLogService auditLogService;

    @Transactional
    @Scheduled(cron = "${app.scheduler.trial-expiry-cron}")
    public void expireTrials() {
        LocalDateTime now = LocalDateTime.now();

        log.info("Checking for expired tenant trials...");

        List<Tenant> expiredTenants = tenantRepository
                .findAllByTenantStatusAndTrialEndsAtBefore(
                        TenantStatus.TRIAL,
                        now
                );

        if (expiredTenants.isEmpty()) {
            log.info("No expired tenant trials found.");
            return;
        }

        for (Tenant tenant : expiredTenants) {

            tenant.setTenantStatus(TenantStatus.SUSPENDED);
            tenant.setSuspendedAt(now);

            auditLogService.log(
                    tenant,
                    AuditAction.SUSPEND,
                    AuditEntityType.TENANT,
                    tenant.getId(),
                    "Trial expired automatically. Tenant suspended."
            );

            log.info(
                    "Tenant '{}' ({}) trial expired and has been suspended.",
                    tenant.getName(),
                    tenant.getId()
            );
        }

        tenantRepository.saveAll(expiredTenants);

        log.info(
                "Trial expiry scheduler completed. {} tenant(s) suspended.",
                expiredTenants.size()
        );
    }
}