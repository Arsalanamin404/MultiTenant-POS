package com.arsalan.tenanttable.administration.tenant.controller;

import com.arsalan.tenanttable.administration.tenant.dto.TenantDetailsResponseDto;
import com.arsalan.tenanttable.administration.tenant.dto.TenantSummaryResponseDto;
import com.arsalan.tenanttable.administration.tenant.dto.UpdateTenantPlanRequestDto;
import com.arsalan.tenanttable.administration.tenant.dto.UpdateTenantStatusRequestDto;
import com.arsalan.tenanttable.administration.tenant.service.IPlatformTenantService;
import com.arsalan.tenanttable.common.dto.ApiResponse;
import com.arsalan.tenanttable.tenant.enums.TenantStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/platform/tenants")
@RequiredArgsConstructor
@Tag(name = "Platform Tenant Management")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class PlatformTenantController {

    private final IPlatformTenantService platformTenantService;

    @GetMapping
    @Operation(summary = "Get all tenants")
    public ResponseEntity<ApiResponse<Page<TenantSummaryResponseDto>>> getTenants(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) TenantStatus status,
            @PageableDefault(
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable,
            HttpServletRequest request
    ) {
        Page<TenantSummaryResponseDto> result = platformTenantService.getTenants(search, status, pageable);
        ApiResponse<Page<TenantSummaryResponseDto>> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Tenants retrieved successfully.",
                result,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tenant by id")
    public ResponseEntity<ApiResponse<TenantDetailsResponseDto>> getTenantById(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {
        TenantDetailsResponseDto result = platformTenantService.getTenantDetails(id);
        ApiResponse<TenantDetailsResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Tenant retrieved successfully.",
                result,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update tenant status")
    public ResponseEntity<ApiResponse<Void>> updateTenantStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTenantStatusRequestDto dto,
            HttpServletRequest request
    ) {
        platformTenantService.updateTenantStatus(id, dto);

        ApiResponse<Void> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Tenant status updated successfully.",
                null,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{tenantId}/plan")
    public ResponseEntity<ApiResponse<Void>> updateTenantPlan(
            @PathVariable UUID tenantId,
            @Valid @RequestBody UpdateTenantPlanRequestDto request,
            HttpServletRequest servletRequest
    ) {

        platformTenantService.updateTenantPlan(tenantId, request);

        ApiResponse<Void> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Tenant plan updated successfully.",
                null,
                servletRequest.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }
}
