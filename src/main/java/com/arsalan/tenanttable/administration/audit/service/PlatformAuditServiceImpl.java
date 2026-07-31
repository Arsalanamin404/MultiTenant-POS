package com.arsalan.tenanttable.administration.audit.service;

import com.arsalan.tenanttable.AuditLog.dto.AuditLogResponseDto;
import com.arsalan.tenanttable.AuditLog.entity.AuditLog;
import com.arsalan.tenanttable.AuditLog.enums.AuditAction;
import com.arsalan.tenanttable.AuditLog.enums.AuditEntityType;
import com.arsalan.tenanttable.AuditLog.mapper.AuditLogMapper;
import com.arsalan.tenanttable.AuditLog.repository.AuditLogRepository;
import com.arsalan.tenanttable.exception.ResourceNotFoundException;
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
public class PlatformAuditServiceImpl implements IPlatformAuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponseDto> getAuditLogs(
            AuditAction action,
            AuditEntityType entityType,
            Pageable pageable
    ) {

        log.info(
                "Fetching audit logs. action={}, entityType={}",
                action,
                entityType
        );

        Page<AuditLog> auditLogs =
                auditLogRepository.findAllForPlatform(
                        action,
                        entityType,
                        pageable
                );

        log.info("Found {} audit logs.", auditLogs.getTotalElements());

        return auditLogs.map(AuditLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLogResponseDto getAuditLog(UUID auditLogId) {

        log.info("Fetching audit log {}", auditLogId);

        AuditLog auditLog = auditLogRepository.findById(auditLogId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Audit Log not found id= " + auditLogId)
                );

        return AuditLogMapper.toDto(auditLog);
    }
}
