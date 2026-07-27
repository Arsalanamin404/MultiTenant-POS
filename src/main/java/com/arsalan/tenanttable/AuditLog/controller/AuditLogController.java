package com.arsalan.tenanttable.AuditLog.controller;

import com.arsalan.tenanttable.AuditLog.dto.AuditLogResponseDto;
import com.arsalan.tenanttable.AuditLog.service.IAuditLogService;
import com.arsalan.tenanttable.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/audit-logs")
public class AuditLogController {
    private final IAuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuditLogResponseDto>>> getAll(
            @PageableDefault(sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable,
            HttpServletRequest request
    ) {

        Page<AuditLogResponseDto> logs = auditLogService.getAll(pageable);

        ApiResponse<Page<AuditLogResponseDto>> response =
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Audit logs fetched successfully.",
                        logs,
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuditLogResponseDto>> getById(
            @Valid UUID id,
            HttpServletRequest request
    ) {

        AuditLogResponseDto log = auditLogService.getById(id);

        ApiResponse<AuditLogResponseDto> response =
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Audit log fetched successfully.",
                        log,
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }
}
