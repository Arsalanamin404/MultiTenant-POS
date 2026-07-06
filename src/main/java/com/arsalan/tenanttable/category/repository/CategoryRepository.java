package com.arsalan.tenanttable.category.repository;

import com.arsalan.tenanttable.category.entity.Category;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByTenantIdAndNameIgnoreCase(UUID tenantId, String name);

    Optional<Category> findByTenantIdAndName(UUID tenantId, String name);

    List<Category>findByNameIgnoreCaseAndTenant(String name, Tenant tenant);

    Page<Category>findAllByTenantId(UUID id, Pageable page);

    Optional<Category> findByIdAndTenantId(UUID id, UUID tenant_id);
}
