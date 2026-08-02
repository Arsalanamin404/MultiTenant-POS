package com.arsalan.tenanttable.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemRequestDto {
    @NotNull(message = "Menu item is required.")
    private UUID menuItemId;

    @Positive(message = "Quantity must be greater than zero.")
    private Integer quantity;

    private String notes;
}
