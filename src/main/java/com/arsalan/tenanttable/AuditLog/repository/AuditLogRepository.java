package com.arsalan.tenanttable.AuditLog.repository;

import com.arsalan.tenanttable.AuditLog.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    Page<AuditLog> findByTenantId(UUID tenantId, Pageable pageable);

    Optional<AuditLog> findByIdAndTenantId(UUID id, UUID tenantId);
}
