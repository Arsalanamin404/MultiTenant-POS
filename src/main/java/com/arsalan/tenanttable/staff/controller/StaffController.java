package com.arsalan.tenanttable.staff.controller;

import com.arsalan.tenanttable.common.dto.ApiResponse;
import com.arsalan.tenanttable.staff.dto.StaffResponseDto;
import com.arsalan.tenanttable.staff.dto.UpdateStaffRoleRequestDto;
import com.arsalan.tenanttable.staff.dto.UpdateStaffStatusRequestDto;
import com.arsalan.tenanttable.staff.service.IStaffService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER')")
public class StaffController {
    private final IStaffService staffService;
/*
TODO:
    GET    /staff
    GET    /staff/{id}
    PATCH  /staff/{id}
    PATCH  /staff/{id}/activate
    PATCH  /staff/{id}/deactivate
    DELETE /staff/{id}
*/

    @GetMapping
    public ResponseEntity<ApiResponse<Page<StaffResponseDto>>> getAllStaff(
            Pageable pageable,
            @NonNull HttpServletRequest request
    ) {
        Page<StaffResponseDto> response = staffService.getAllStaff(pageable);

        ApiResponse<Page<StaffResponseDto>> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Staff members retrieved successfully.",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StaffResponseDto>> getStaffById(
            @PathVariable UUID id,
            @NonNull HttpServletRequest request
    ) {
        StaffResponseDto response = staffService.getStaffById(id);

        ApiResponse<StaffResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Staff retrieved successfully.",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<ApiResponse<StaffResponseDto>> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStaffRoleRequestDto dto,
            @NonNull HttpServletRequest request
    ) {

        StaffResponseDto response = staffService.updateRole(id, dto);

        ApiResponse<StaffResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Staff role updated successfully.",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<StaffResponseDto>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStaffStatusRequestDto dto,
            HttpServletRequest request
    ) {

        StaffResponseDto response = staffService.updateStatus(id, dto);

        ApiResponse<StaffResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Staff status updated successfully.",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }
}
