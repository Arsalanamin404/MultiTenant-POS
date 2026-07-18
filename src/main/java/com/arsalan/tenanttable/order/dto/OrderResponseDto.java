package com.arsalan.tenanttable.order.dto;

import com.arsalan.tenanttable.order.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponseDto {
    private UUID id;

    private Long orderNumber;

    private UUID diningTableId;

    private String diningTableNumber;

    private OrderStatus status;

    private List<OrderItemResponseDto> items;

    private BigDecimal subTotal;

    private BigDecimal taxRate;
    private BigDecimal taxAmount;

    private BigDecimal discountRate;
    private BigDecimal discountAmount;

    private BigDecimal totalAmount;

    private String notes;

    private String createdByName;

    private Instant createdAt;

    private Instant updatedAt;
}
