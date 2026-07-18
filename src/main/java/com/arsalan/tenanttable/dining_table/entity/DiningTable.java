package com.arsalan.tenanttable.dining_table.entity;

import com.arsalan.tenanttable.dining_table.enums.DiningTableStatus;
import com.arsalan.tenanttable.exception.InvalidOperationException;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.Instant;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "dining_tables",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_table_tenant_name",
                        columnNames = {"tenant_id", "table_number"}
                )
        },
        indexes = {
                @Index(name = "idx_table_tenant", columnList = "tenant_id"),
                @Index(name = "idx_table_status", columnList = "status"),
                @Index(name = "idx_table_tenant_status", columnList = "tenant_id,status")
        }
)
public class DiningTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String tableNumber;

    @Column(nullable = false)
    private int capacity;

    @Column(length = 250)
    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private DiningTableStatus status = DiningTableStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_id")
    private User updatedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public void occupy(User user) {
        if (this.status != DiningTableStatus.AVAILABLE && status != DiningTableStatus.RESERVED) {
            throw new InvalidOperationException("Cannot occupy a table that is " + status);
        }

        this.status = DiningTableStatus.OCCUPIED;
        this.updatedBy = user;
    }

    public void makeAvailable(User user) {
        if (status == DiningTableStatus.AVAILABLE) {
            return;
        }

        this.status = DiningTableStatus.AVAILABLE;
        this.updatedBy = user;
    }

    public void reserve(User user) {
        if (this.status != DiningTableStatus.AVAILABLE) {
            throw new InvalidOperationException("Cannot reserve a table that is " + status);
        }

        this.status = DiningTableStatus.RESERVED;
        this.updatedBy = user;
    }
}
