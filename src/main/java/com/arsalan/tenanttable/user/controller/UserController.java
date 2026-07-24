package com.arsalan.tenanttable.user.controller;

import com.arsalan.tenanttable.common.dto.ApiResponse;
import com.arsalan.tenanttable.user.dto.AllUsersResponseDto;
import com.arsalan.tenanttable.user.dto.ChangePasswordRequestDto;
import com.arsalan.tenanttable.user.dto.UpdateProfileRequestDto;
import com.arsalan.tenanttable.user.dto.UserResponseDto;
import com.arsalan.tenanttable.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<ApiResponse<List<AllUsersResponseDto>>> getAllUsers(
            HttpServletRequest request
    ) {
        List<AllUsersResponseDto> users = userService.getAllUsers();
        ApiResponse<List<AllUsersResponseDto>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Users fetched successfully",
                users,
                request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }
}
