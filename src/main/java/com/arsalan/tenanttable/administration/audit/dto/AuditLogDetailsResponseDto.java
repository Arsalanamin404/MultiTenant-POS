package com.arsalan.tenanttable.administration.audit.dto;

import com.arsalan.tenanttable.AuditLog.enums.AuditAction;
import com.arsalan.tenanttable.AuditLog.enums.AuditEntityType;
import com.arsalan.tenanttable.user.dto.UserSummaryResponseDto;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record AuditLogDetailsResponseDto(
        UUID id,
        UUID entityId,
        AuditEntityType entityType,
        AuditAction action,
        String description,
        UserSummaryResponseDto performedBy,
        Instant createdAt
) {
}
