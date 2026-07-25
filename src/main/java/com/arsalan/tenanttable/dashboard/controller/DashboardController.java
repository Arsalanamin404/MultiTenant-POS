package com.arsalan.tenanttable.dashboard.controller;

import com.arsalan.tenanttable.common.dto.ApiResponse;
import com.arsalan.tenanttable.dashboard.dto.DashboardResponseDto;
import com.arsalan.tenanttable.dashboard.service.IDashboardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
public class DashboardController {
    private final IDashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponseDto>> getDashboard(
            HttpServletRequest request
    ) {
        DashboardResponseDto response = dashboardService.getDashboard();

        ApiResponse<DashboardResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Dashboard retrieved successfully.",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }
}
