package com.arsalan.tenanttable.administration.audit.dto;

import com.arsalan.tenanttable.AuditLog.enums.AuditAction;
import com.arsalan.tenanttable.AuditLog.enums.AuditEntityType;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record AuditLogSummaryResponseDto(
        UUID id,
        String performedBy,
        AuditAction action,
        AuditEntityType entityType,
        UUID entityId,
        String description,
        Instant createdAt
) {
}
