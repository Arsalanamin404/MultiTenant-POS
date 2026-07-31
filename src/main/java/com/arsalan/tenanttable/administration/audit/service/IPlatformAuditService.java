package com.arsalan.tenanttable.administration.audit.service;

import com.arsalan.tenanttable.AuditLog.dto.AuditLogResponseDto;
import com.arsalan.tenanttable.AuditLog.enums.AuditAction;
import com.arsalan.tenanttable.AuditLog.enums.AuditEntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IPlatformAuditService {

    Page<AuditLogResponseDto> getAuditLogs(
            AuditAction action,
            AuditEntityType entityType,
            Pageable pageable
    );

    AuditLogResponseDto getAuditLog(UUID auditLogId);

}
