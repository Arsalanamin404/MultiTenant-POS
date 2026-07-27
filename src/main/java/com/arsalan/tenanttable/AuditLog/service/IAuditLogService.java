package com.arsalan.tenanttable.AuditLog.service;

import com.arsalan.tenanttable.AuditLog.dto.AuditLogResponseDto;
import com.arsalan.tenanttable.AuditLog.enums.AuditAction;
import com.arsalan.tenanttable.AuditLog.enums.AuditEntityType;
import com.arsalan.tenanttable.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IAuditLogService {
    void log(
            AuditAction action,
            AuditEntityType entityType,
            UUID entityId,
            String description
    );

    void log(
            User performedBy,
            AuditAction action,
            AuditEntityType entityType,
            UUID entityId,
            String description
    );

    Page<AuditLogResponseDto> getAll(Pageable pageable);

    AuditLogResponseDto getById(UUID id);
}
