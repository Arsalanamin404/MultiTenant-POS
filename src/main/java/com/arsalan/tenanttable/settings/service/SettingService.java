package com.arsalan.tenanttable.settings.service;

import com.arsalan.tenanttable.AuditLog.enums.AuditAction;
import com.arsalan.tenanttable.AuditLog.enums.AuditEntityType;
import com.arsalan.tenanttable.AuditLog.service.IAuditLogService;
import com.arsalan.tenanttable.common.utils.ICurrentUserUtilService;
import com.arsalan.tenanttable.exception.ResourceNotFoundException;
import com.arsalan.tenanttable.settings.dto.SettingsResponseDto;
import com.arsalan.tenanttable.settings.dto.UpdateSettingsRequestDto;
import com.arsalan.tenanttable.settings.entity.Settings;
import com.arsalan.tenanttable.settings.mapper.SettingsMapper;
import com.arsalan.tenanttable.settings.repository.SettingsRepository;
import com.arsalan.tenanttable.user.entity.User;
import com.arsalan.tenanttable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SettingService implements ISettingService {
    private final SettingsRepository settingsRepository;
    private final ICurrentUserUtilService currentUserUtilService;
    private final IAuditLogService auditLogService;
    private final UserRepository userRepository;

    private User getOrThrowCurrentUser() {
        UUID userId = currentUserUtilService.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Settings getOrThrowSettings() {
        UUID tenantId = currentUserUtilService.getCurrentTenantId();

        return settingsRepository.findByTenantId(tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("SETTINGS_NOT_FOUND"));
    }

    @Override
    @Transactional(readOnly = true)
    public SettingsResponseDto getSettings() {

        Settings settings = getOrThrowSettings();

        log.info("Settings fetched for tenantId={}",
                settings.getTenant().getId());

        return SettingsMapper.toDto(settings);
    }

    @Override
    @Transactional
    public SettingsResponseDto updateSettings(UpdateSettingsRequestDto dto) {
        User currentUser = getOrThrowCurrentUser();
        Settings settings = getOrThrowSettings();

        log.info("Updating settings for tenantId={}",
                settings.getTenant().getId());

        settings.update(dto);

        auditLogService.log(
                currentUser,
                AuditAction.UPDATE,
                AuditEntityType.SETTINGS,
                settings.getId(),
                "Settings updated successfully for tenantId: " + settings.getTenant().getId()
        );

        log.info("Settings updated successfully for tenantId={}",
                settings.getTenant().getId());

        return SettingsMapper.toDto(settings);
    }
}
