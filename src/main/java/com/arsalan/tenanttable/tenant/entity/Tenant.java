package com.arsalan.tenanttable.tenant.entity;

import com.arsalan.tenanttable.tenant.enums.PlanType;
import com.arsalan.tenanttable.tenant.enums.TenantStatus;
import com.arsalan.tenanttable.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.arsalan.tenanttable.category.entity.Category;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "tenants",
        indexes = {
                @Index(name = "idx_tenant_status", columnList = "tenant_status"),
                @Index(name = "idx_tenant_plan", columnList = "plan_type"),
                @Index(name = "idx_tenant_name", columnList = "name")
        }
)
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TenantStatus tenantStatus = TenantStatus.TRIAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType planType = PlanType.FREE;

    private LocalDateTime trialEndsAt;
    private LocalDateTime suspendedAt;

    @OneToMany(mappedBy = "tenant",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
