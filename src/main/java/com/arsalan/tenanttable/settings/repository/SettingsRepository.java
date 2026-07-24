package com.arsalan.tenanttable.settings.repository;

import com.arsalan.tenanttable.settings.entity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SettingsRepository extends JpaRepository<Settings, UUID> {
    Optional<Settings> findByTenantId(UUID tenantId);
}
