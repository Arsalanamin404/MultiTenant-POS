package com.arsalan.tenanttable.coupon.repository;

import com.arsalan.tenanttable.coupon.entity.Coupon;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    Optional<Coupon> findByIdAndTenant(UUID id, Tenant tenant);

    Optional<Coupon> findByCodeAndTenant(String code, Tenant tenant);

    boolean existsByCodeAndTenant(String code, Tenant tenant);

    Page<Coupon> findAllByTenant(Tenant tenant, Pageable pageable);
    
}
