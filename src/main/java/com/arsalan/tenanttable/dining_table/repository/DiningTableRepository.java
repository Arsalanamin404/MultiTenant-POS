package com.arsalan.tenanttable.dining_table.repository;

import com.arsalan.tenanttable.dining_table.entity.DiningTable;
import com.arsalan.tenanttable.dining_table.enums.DiningTableStatus;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DiningTableRepository extends JpaRepository<DiningTable, UUID> {
    Optional<DiningTable> findByIdAndTenant(UUID id, Tenant tenant);

    Page<DiningTable> findAllByTenant(Tenant tenant, Pageable pageable);

    boolean existsByTableNumberAndTenant(String tableNumber, Tenant tenant);

    Optional<DiningTable> findByTableNumberAndTenant(String tableNumber, Tenant tenant);

    long countByTenant(Tenant tenant);

    Page<DiningTable> findAllByTenantAndStatus(DiningTableStatus status, Tenant tenant, Pageable pageable);
}
