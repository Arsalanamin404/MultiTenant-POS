package com.arsalan.tenanttable.order.repository;

import com.arsalan.tenanttable.order.entity.Order;
import com.arsalan.tenanttable.order.enums.OrderStatus;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByIdAndTenant(UUID id, Tenant tenant);

    Page<Order> findByTenantAndStatusIn(Tenant tenant, List<OrderStatus> status, Pageable pageable);

    Optional<Order> findTopByTenantOrderByOrderNumberDesc(Tenant tenant);

    long countByTenantId(UUID tenantId);

    Long countByTenantIdAndStatus(
            UUID tenantId,
            OrderStatus status
    );

    @Query("""
            SELECT COUNT(o)
            FROM Order o
            WHERE o.tenant.id = :tenantId
            AND o.createdAt >= :start
            AND o.createdAt < :end
            """)
    long countOrdersBetween(
            UUID tenantId,
            Instant start,
            Instant end
    );

    @Query("""
            SELECT COUNT(o)
            FROM Order o
            WHERE o.tenant.id = :tenantId
            AND o.status = :status
            AND o.createdAt >= :start
            AND o.createdAt < :end
            """)
    long countOrdersBetweenAndStatus(
            UUID tenantId,
            OrderStatus status,
            Instant start,
            Instant end
    );

    @Query("""
            SELECT COUNT(o)
            FROM Order o
            WHERE o.tenant.id = :tenantId
            AND o.status IN :statuses
            AND o.createdAt >= :start
            AND o.createdAt < :end
            """)
    long countOrdersBetweenAndStatuses(
            UUID tenantId,
            Collection<OrderStatus> statuses,
            Instant start,
            Instant end
    );
}
