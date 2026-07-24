package com.arsalan.tenanttable.settings.service;

import com.arsalan.tenanttable.settings.dto.SettingsResponseDto;
import com.arsalan.tenanttable.settings.dto.UpdateSettingsRequestDto;

public interface ISettingService {
    SettingsResponseDto getSettings();

    SettingsResponseDto updateSettings(UpdateSettingsRequestDto dto);
}
