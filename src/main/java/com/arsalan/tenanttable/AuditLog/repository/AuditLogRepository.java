package com.arsalan.tenanttable.AuditLog.repository;

import com.arsalan.tenanttable.AuditLog.entity.AuditLog;
import com.arsalan.tenanttable.AuditLog.enums.AuditAction;
import com.arsalan.tenanttable.AuditLog.enums.AuditEntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    Page<AuditLog> findByTenantId(UUID tenantId, Pageable pageable);

    Optional<AuditLog> findByIdAndTenantId(UUID id, UUID tenantId);

    @Query("""
            SELECT a
            FROM AuditLog a
            WHERE
            (:action IS NULL OR a.action = :action)
            AND
            (:entityType IS NULL OR a.entityType = :entityType)
            """)
    Page<AuditLog> findAllForPlatform(
            AuditAction action,
            AuditEntityType entityType,
            Pageable pageable
    );
}
