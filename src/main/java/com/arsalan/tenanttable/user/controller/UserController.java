package com.arsalan.tenanttable.user.controller;

import com.arsalan.tenanttable.common.dto.ApiResponse;
import com.arsalan.tenanttable.user.dto.*;
import com.arsalan.tenanttable.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> getMe(@NonNull HttpServletRequest request) {
        UserResponseDto profile = userService.getMe();

        ApiResponse<UserResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "USER_PROFILE_FETCHED_SUCCESSFULLY",
                profile,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }


    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequestDto dto,
            @NonNull HttpServletRequest request
    ) {
        UserResponseDto profile = userService.updateMyProfile(dto);

        ApiResponse<UserResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "USER_PROFILE_UPDATED_SUCCESSFULLY",
                profile,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<UserResponseDto>> changePassword(
            @Valid @RequestBody ChangePasswordRequestDto dto,
            @NonNull HttpServletRequest request
    ) {
        UserResponseDto profile = userService.changePassword(dto);

        ApiResponse<UserResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "PASSWORD_CHANGED_SUCCESSFULLY",
                profile,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserSummaryResponseDto>>> getAllUsers(
            Pageable pageable,
            HttpServletRequest request
    ) {
        Page<UserSummaryResponseDto> users = userService.getUsers(pageable);

        ApiResponse<Page<UserSummaryResponseDto>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Users fetched successfully",
                users,
                request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUser(
            @Valid UUID id,
            HttpServletRequest request
    ) {
        UserResponseDto user = userService.getUserById(id);

        ApiResponse<UserResponseDto> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "User fetched successfully",
                user,
                request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUserStatus(
            @Valid UUID id,
            @Valid @RequestBody UpdateUserStatusRequestDto dto,
            HttpServletRequest request
    ) {
        UserResponseDto user = userService.updateUserStatus(id, dto);

        ApiResponse<UserResponseDto> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "User status updated successfully",
                user,
                request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }
}
