package com.arsalan.tenanttable.settings.service;

import com.arsalan.tenanttable.common.utils.ICurrentUserUtilService;
import com.arsalan.tenanttable.exception.ResourceNotFoundException;
import com.arsalan.tenanttable.settings.dto.SettingsResponseDto;
import com.arsalan.tenanttable.settings.dto.UpdateSettingsRequestDto;
import com.arsalan.tenanttable.settings.entity.Settings;
import com.arsalan.tenanttable.settings.mapper.SettingsMapper;
import com.arsalan.tenanttable.settings.repository.SettingsRepository;
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

        Settings settings = getOrThrowSettings();

        log.info("Updating settings for tenantId={}",
                settings.getTenant().getId());

        settings.update(dto);

        log.info("Settings updated successfully for tenantId={}",
                settings.getTenant().getId());

        return SettingsMapper.toDto(settings);
    }
}
