package com.arsalan.tenanttable.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderItemRequestDto {
    @NotNull(message = "Menu item is required.")
    private UUID menuItemId;

    @Positive(message = "Quantity must be greater than zero.")
    private Integer quantity;

    private String notes;
}
