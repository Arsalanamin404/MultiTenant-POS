package com.arsalan.tenanttable.tenant.repository;

import com.arsalan.tenanttable.administration.tenant.dto.TenantSummaryProjection;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.tenant.enums.TenantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    boolean existsByNameIgnoreCase(String name);

    long countByTenantStatus(TenantStatus status);

    @Query("""
                SELECT new com.arsalan.tenanttable.administration.tenant.dto.TenantSummaryProjection(
                    t.id,
                    t.name,
                    t.tenantStatus,
                    t.planType,
                    u.fullName,
                    u.email,
                    t.createdAt
                )
                FROM Tenant t
                JOIN t.users u
                WHERE
                    u.tenantRole = 'OWNER'
                    AND (:status IS NULL OR t.tenantStatus = :status)
                    AND (
                        :search IS NULL
                        OR LOWER(t.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
                    )
            """)
    Page<TenantSummaryProjection> findAllForPlatform(
            @Param("search") String search,
            @Param("status") TenantStatus status,
            Pageable pageable
    );

    List<Tenant> findAllByTenantStatusAndTrialEndsAtBefore(
            TenantStatus status,
            LocalDateTime dateTime
    );
}
