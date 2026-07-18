package com.arsalan.tenanttable.order.repository;

import com.arsalan.tenanttable.dining_table.entity.DiningTable;
import com.arsalan.tenanttable.order.entity.Order;
import com.arsalan.tenanttable.order.enums.OrderStatus;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByIdAndTenant(UUID id, Tenant tenant);

    Page<Order> findByTenant(Tenant tenant, Pageable pageable);

    Page<Order> findByTenantAndStatusIn(Tenant tenant, List<OrderStatus> status, Pageable pageable);

    Page<Order> findByTenantAndDiningTableAndStatus(Tenant tenant, DiningTable table, OrderStatus status, Pageable pageable);

    boolean existsByOrderNumberAndTenant(Long orderNumber, Tenant tenant);

    Optional<Order> findTopByTenantOrderByOrderNumberDesc(Tenant tenant);

    long countByTenant(Tenant tenant);

    long countByTenantAndStatus(Tenant tenant, OrderStatus status);
}
