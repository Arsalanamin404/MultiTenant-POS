package com.arsalan.tenanttable.AuditLog.mapper;

import com.arsalan.tenanttable.AuditLog.dto.AuditLogResponseDto;
import com.arsalan.tenanttable.AuditLog.entity.AuditLog;
import com.arsalan.tenanttable.user.entity.User;

public final class AuditLogMapper {
    private AuditLogMapper() {
    }

    public static AuditLogResponseDto toDto(AuditLog log) {
        User performedBy = log.getPerformedBy();
        return AuditLogResponseDto.builder()
                .id(log.getId())
                .performedById(
                        performedBy != null ? performedBy.getId() : null
                )

                .performedByName(
                        performedBy != null ? performedBy.getFullName() : "SYSTEM"
                )
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .description(log.getDescription())
                .ipAddress(log.getIpAddress())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
