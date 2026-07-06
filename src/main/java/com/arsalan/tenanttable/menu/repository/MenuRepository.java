package com.arsalan.tenanttable.menu.repository;

import com.arsalan.tenanttable.menu.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<MenuItem, UUID> {
    Optional<MenuItem> findByIdAndTenantId(UUID id, UUID tenantId);

    Page<MenuItem> findByTenantId(UUID tenantId, Pageable pageable);

    Page<MenuItem> findByCategoryIdAndTenantId(UUID categoryId, UUID tenantId, Pageable pageable);

    Optional<MenuItem> findByNameIgnoreCaseAndTenantId(String name, UUID id);

    boolean existsByNameIgnoreCaseAndTenantId(String name, UUID tenantId);

    Page<MenuItem> findAllByTenantId(UUID id, Pageable pageable);
}
