package com.arsalan.tenanttable.user.repository;

import com.arsalan.tenanttable.common.enums.PlatformRole;
import com.arsalan.tenanttable.common.enums.TenantRole;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Boolean existsByEmail(String email);

    Boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);

    boolean existsByPlatformRole(PlatformRole platformRole);

    Optional<User> findByIdAndTenant(UUID id, Tenant tenant);

    @Query("""
            SELECT u
            FROM User u
            JOIN FETCH u.tenant
            WHERE u.tenant.id = :id
                 AND u.tenantRole = :role
            """)
    List<User> findAllByTenantIdAndTenantRole(UUID id, TenantRole role);

    Page<User> findAllByTenant(Tenant tenant, Pageable pageable);
}
