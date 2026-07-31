package com.arsalan.tenanttable.administration.audit.controller;

import com.arsalan.tenanttable.AuditLog.dto.AuditLogResponseDto;
import com.arsalan.tenanttable.AuditLog.enums.AuditAction;
import com.arsalan.tenanttable.AuditLog.enums.AuditEntityType;
import com.arsalan.tenanttable.administration.audit.service.IPlatformAuditService;
import com.arsalan.tenanttable.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/platform/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Platform Audit Logs", description = "Platform audit log management APIs")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class PlatformAuditController {

    private final IPlatformAuditService platformAuditService;

    @GetMapping
    @Operation(summary = "Get all audit logs")
    public ResponseEntity<ApiResponse<Page<AuditLogResponseDto>>> getAuditLogs(
            @RequestParam(required = false) AuditAction action,
            @RequestParam(required = false) AuditEntityType entityType,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            HttpServletRequest request
    ) {

        Page<AuditLogResponseDto> auditLogs = platformAuditService.getAuditLogs(
                action,
                entityType,
                pageable
        );

        ApiResponse<Page<AuditLogResponseDto>> response =
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Audit logs retrieved successfully.",
                        auditLogs,
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get audit log by id")
    public ResponseEntity<ApiResponse<AuditLogResponseDto>> getAuditLog(
            @PathVariable UUID id, HttpServletRequest request
    ) {
        AuditLogResponseDto auditLog = platformAuditService.getAuditLog(id);
        ApiResponse<AuditLogResponseDto> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Audit log retrieved successfully.",
                auditLog,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }
}