package com.arsalan.tenanttable.staff.entity;

import com.arsalan.tenanttable.common.enums.TenantRole;
import com.arsalan.tenanttable.staff.enums.InvitationStatus;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(
        name = "staff_invitations",
        indexes = {
                @Index(name = "idx_staff_invitation_email", columnList = "email"),
                @Index(name = "idx_staff_invitation_token", columnList = "token")
        }
)
public class StaffInvitation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TenantRole tenantRole;

    @Column(nullable = false, unique = true)
    private String token;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status = InvitationStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private Instant expiresAt;

    private Instant acceptedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by_id", nullable = false)
    private User invitedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public boolean isExpired() {
        return expiresAt.isBefore(Instant.now());
    }
}
