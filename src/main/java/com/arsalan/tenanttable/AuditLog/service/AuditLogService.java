package com.arsalan.tenanttable.AuditLog.service;

import com.arsalan.tenanttable.AuditLog.dto.AuditLogResponseDto;
import com.arsalan.tenanttable.AuditLog.entity.AuditLog;
import com.arsalan.tenanttable.AuditLog.enums.AuditAction;
import com.arsalan.tenanttable.AuditLog.enums.AuditEntityType;
import com.arsalan.tenanttable.AuditLog.mapper.AuditLogMapper;
import com.arsalan.tenanttable.AuditLog.repository.AuditLogRepository;
import com.arsalan.tenanttable.common.utils.ICurrentUserUtilService;
import com.arsalan.tenanttable.exception.ResourceNotFoundException;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.tenant.repository.TenantRepository;
import com.arsalan.tenanttable.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService implements IAuditLogService {
    private final ICurrentUserUtilService currentUserUtilService;
    private final AuditLogRepository auditLogRepository;
    private final TenantRepository tenantRepository;
    private final HttpServletRequest request;

    private Tenant getCurrentTenant() {
        UUID tenantId = currentUserUtilService.getCurrentTenantId();
        if (tenantId == null) {
            return null;
        }
        return tenantRepository.findById(tenantId).orElse(null);
    }

    @Override
    public void log(
            AuditAction action,
            AuditEntityType entityType,
            UUID entityId,
            String description
    ) {
        log.debug(
                "Recording audit log: action={}, entityType={}, entityId={}",
                action,
                entityType,
                entityId
        );
        Tenant tenant = getCurrentTenant();

        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        AuditLog auditLog = AuditLog.builder()
                .tenant(tenant)
                .performedBy(null)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .description(description)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        auditLogRepository.save(auditLog);
        log.debug("User ip '{}' performed '{}' on  {} '{}'",
                ipAddress, action, entityType, entityId);
    }

    @Override
    public void log(User performedBy, AuditAction action, AuditEntityType entityType, UUID entityId, String description) {
        log.debug(
                "Recording audit log: action={}, entityType={}, entityId={}, userId={}",
                action,
                entityType,
                entityId,
                performedBy.getId()
        );

        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        AuditLog auditLog = AuditLog.builder()
                .tenant(performedBy.getTenant())
                .performedBy(performedBy)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .description(description)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        auditLogRepository.save(auditLog);
        log.debug("User '{}' performed '{}' on  {} '{}'",
                performedBy, action, entityType, entityId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponseDto> getAll(Pageable pageable) {
        UUID tenantId = currentUserUtilService.getCurrentTenantId();

        return auditLogRepository.findByTenantId(tenantId, pageable)
                .map(AuditLogMapper::toDto);
    }

    @Override
    public AuditLogResponseDto getById(UUID id) {
        UUID tenantId = currentUserUtilService.getCurrentTenantId();
        AuditLog log = auditLogRepository
                .findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Log not found with id: " + id));

        return AuditLogMapper.toDto(log);
    }
}
