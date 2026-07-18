package com.arsalan.tenanttable.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class OrderItemResponseDto {
    private UUID id;

    private UUID menuItemId;

    private String menuItemName;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal lineTotal;

    private String notes;
}
