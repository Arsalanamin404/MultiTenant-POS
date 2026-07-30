package com.arsalan.tenanttable.coupon.entity;

import com.arsalan.tenanttable.coupon.enums.CouponType;
import com.arsalan.tenanttable.exception.InvalidOperationException;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "coupons",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_coupon_code_tenant", columnNames = {"code", "tenant_id"})
        }
)
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType couponType = CouponType.PERCENTAGE;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    @Builder.Default
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal minimumOrderAmount = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal maximumDiscount;

    @Builder.Default
    private Integer usageLimit = 5;

    @Builder.Default
    private Integer usedCount = 0;

    private Instant startsAt;

    private Instant expiresAt;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;


    public void incrementUsage() {
        if (usedCount >= usageLimit) {
            throw new InvalidOperationException("Coupon usage limit exceeded.");
        }
        usedCount++;
    }
}
