package com.arsalan.tenanttable.order.entity;

import com.arsalan.tenanttable.dining_table.entity.DiningTable;
import com.arsalan.tenanttable.exception.InvalidOperationException;
import com.arsalan.tenanttable.order.enums.DiscountType;
import com.arsalan.tenanttable.order.enums.OrderStatus;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(
        name = "orders",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_order_tenant_number", columnNames = {"tenant_id", "order_number"})
        },
        indexes = {
                @Index(name = "idx_order_tenant", columnList = "tenant_id"),
                @Index(name = "idx_order_table", columnList = "dining_table_id"),
                @Index(name = "idx_order_status", columnList = "status"),
                @Index(name = "idx_order_tenant_status", columnList = "tenant_id, status")
        }
)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // generate this from service
    @Column(nullable = false)
    private Long orderNumber;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dining_table_id", nullable = false)
    private DiningTable diningTable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Builder.Default
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subTotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    // this field is used for upcoming versions
    private DiscountType discountType = DiscountType.PERCENTAGE;

    @Builder.Default
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal discountRate = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_id")
    private User updatedBy;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;


    public void addItem(OrderItem orderItem) {
        orderItem.calculateLineTotal();
        items.add(orderItem);
        orderItem.setOrder(this);
    }

    public void removeItem(OrderItem orderItem) {
        orderItem.calculateLineTotal();
        items.remove(orderItem);
        orderItem.setOrder(this);
    }

    public boolean isEditable() {
        return status != OrderStatus.COMPLETED && status != OrderStatus.CANCELLED && status != OrderStatus.PAID;
    }

    public boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this.status) {
            // If the ORDER_STATUS is currently PENDING,
            // it may only become CONFIRMED or CANCELLED.
            case PENDING -> newStatus == OrderStatus.CONFIRMED
                    || newStatus == OrderStatus.CANCELLED;

            case CONFIRMED -> newStatus == OrderStatus.PREPARING
                    || newStatus == OrderStatus.CANCELLED;

            case PREPARING -> newStatus == OrderStatus.READY;

            case READY -> newStatus == OrderStatus.SERVED;

            case SERVED -> newStatus == OrderStatus.COMPLETED;

            // If the order is already
            // COMPLETED, CANCELLED, or PAID
            // it cannot transition to any other status.
            case COMPLETED, CANCELLED, PAID, REJECTED -> false;
        };
    }

    public void changeStatus(OrderStatus newStatus) {
        if (!canTransitionTo(newStatus)) {
            throw new InvalidOperationException(
                    "Cannot change order status from " + status + " to " + newStatus
            );
        }
    }
}
