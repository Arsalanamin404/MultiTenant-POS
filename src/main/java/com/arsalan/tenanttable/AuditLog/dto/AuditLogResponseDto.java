package com.arsalan.tenanttable.AuditLog.dto;

import com.arsalan.tenanttable.AuditLog.enums.AuditAction;
import com.arsalan.tenanttable.AuditLog.enums.AuditEntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class AuditLogResponseDto {
    private UUID id;
    private UUID performedById;
    private String performedByName;
    private AuditAction action;
    private AuditEntityType entityType;
    private UUID entityId;
    private String description;
    private String ipAddress;
    private Instant createdAt;
}
