package com.arsalan.tenanttable.tenant.repository;

import com.arsalan.tenanttable.tenant.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    boolean existsByNameIgnoreCase(String name);
}
