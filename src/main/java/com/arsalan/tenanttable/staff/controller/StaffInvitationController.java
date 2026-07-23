package com.arsalan.tenanttable.staff.controller;

import com.arsalan.tenanttable.common.dto.ApiResponse;
import com.arsalan.tenanttable.staff.dto.AcceptInvitationRequestDto;
import com.arsalan.tenanttable.staff.dto.InviteStaffRequestDto;
import com.arsalan.tenanttable.staff.dto.StaffInvitationResponseDto;
import com.arsalan.tenanttable.staff.service.IStaffInvitationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
public class StaffInvitationController {
    private final IStaffInvitationService staffInvitationService;

/*
TODO:
    POST   /invitations
    GET    /invitations
    GET    /invitations/{id}
    PATCH  /invitations/{id}/resend
    PATCH  /invitations/{id}/cancel
    POST   /invitations/accept
*/

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<StaffInvitationResponseDto>> invite(
            @Valid @RequestBody InviteStaffRequestDto dto,
            HttpServletRequest request
    ) {
        StaffInvitationResponseDto response = staffInvitationService.invite(dto);
        ApiResponse<StaffInvitationResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Staff invitation created. An invitation email will be sent shortly.",
                response,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Page<StaffInvitationResponseDto>>> getAllInvitations(
            Pageable pageable,
            HttpServletRequest request
    ) {
        Page<StaffInvitationResponseDto> response = staffInvitationService.getAllInvitations(pageable);

        ApiResponse<Page<StaffInvitationResponseDto>> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Staff invitations retrieved successfully.",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<StaffInvitationResponseDto>> getInvitationById(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {
        StaffInvitationResponseDto response = staffInvitationService.getByInvitationId(id);

        ApiResponse<StaffInvitationResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Staff invitation retrieved successfully.",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{id}/resend")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<StaffInvitationResponseDto>> resendInvitation(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {
        StaffInvitationResponseDto response = staffInvitationService.resendInvitation(id);

        ApiResponse<StaffInvitationResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Staff invitation resent successfully.",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<StaffInvitationResponseDto>> cancelInvitation(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {
        StaffInvitationResponseDto response = staffInvitationService.cancelInvitation(id);

        ApiResponse<StaffInvitationResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Staff invitation cancelled successfully.",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<StaffInvitationResponseDto>> acceptStaffInvitation(
            @Valid @RequestBody AcceptInvitationRequestDto dto,
            HttpServletRequest request
    ) {
        StaffInvitationResponseDto response = staffInvitationService.acceptInvitation(dto);

        ApiResponse<StaffInvitationResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Invitation accepted and staff account created successfully.",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

}
