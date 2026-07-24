package com.arsalan.tenanttable.settings.controller;

import com.arsalan.tenanttable.common.dto.ApiResponse;
import com.arsalan.tenanttable.settings.dto.SettingsResponseDto;
import com.arsalan.tenanttable.settings.dto.UpdateSettingsRequestDto;
import com.arsalan.tenanttable.settings.service.ISettingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/settings")
@PreAuthorize("OWNER")
public class SettingsController {
    private final ISettingService settingService;


    @GetMapping
    public ResponseEntity<ApiResponse<SettingsResponseDto>> getSettings(
            HttpServletRequest request
    ) {
        SettingsResponseDto response = settingService.getSettings();

        ApiResponse<SettingsResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "SETTINGS_FETCHED_SUCCESSFULLY",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<SettingsResponseDto>> updateSettings(
            @Valid @RequestBody UpdateSettingsRequestDto dto,
            HttpServletRequest request
    ) {
        SettingsResponseDto response = settingService.updateSettings(dto);

        ApiResponse<SettingsResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "SETTINGS_UPDATED_SUCCESSFULLY",
                response,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }
}
