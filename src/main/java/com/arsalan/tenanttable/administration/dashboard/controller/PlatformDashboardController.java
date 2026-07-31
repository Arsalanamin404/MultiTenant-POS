package com.arsalan.tenanttable.administration.dashboard.controller;

import com.arsalan.tenanttable.administration.dashboard.dto.PlatformDashboardResponseDto;
import com.arsalan.tenanttable.administration.dashboard.service.IPlatformDashboardService;
import com.arsalan.tenanttable.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/platform/dashboard")
@RequiredArgsConstructor
@Tag(name = "Platform Dashboard")
public class PlatformDashboardController {

    private final IPlatformDashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PlatformDashboardResponseDto>> getDashboard(HttpServletRequest request) {
        PlatformDashboardResponseDto dashboard = dashboardService.getDashboard();

        ApiResponse<PlatformDashboardResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Platform dashboard retrieved successfully.",
                dashboard,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }
}
