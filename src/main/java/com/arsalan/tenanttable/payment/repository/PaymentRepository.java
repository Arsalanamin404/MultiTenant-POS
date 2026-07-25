package com.arsalan.tenanttable.payment.repository;

import com.arsalan.tenanttable.order.entity.Order;
import com.arsalan.tenanttable.payment.entity.Payment;
import com.arsalan.tenanttable.payment.enums.PaymentMethod;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByIdAndTenant(UUID id, Tenant tenant);

    Optional<Payment> findByOrder(Order order);

    boolean existsByOrder(Order order);

    Page<Payment> findAllByTenant(Tenant tenant, Pageable pageable);

    Page<Payment> findAllByTenantAndPaymentMethod(
            Tenant tenant,
            PaymentMethod paymentMethod,
            Pageable pageable
    );

    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Payment p
            WHERE p.tenant.id = :tenantId
            """)
    BigDecimal sumOverallRevenue(@Param("tenantId") UUID tenantId);

    @Query("""
            SELECT COALESCE(SUM(p.amount),0)
            FROM Payment p
            WHERE p.tenant.id = :tenantId
            AND p.createdAt >= :start
            AND p.createdAt < :end
            """)
    BigDecimal sumRevenueBetween(
            @Param("tenantId") UUID tenantId,
            @Param("start") Instant start,
            @Param("end") Instant end
    );
}
