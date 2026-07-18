package com.arsalan.tenanttable.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CreateOrderRequestDto {
    @NotNull(message = "Dining table is required.")
    private UUID diningTableId;

    @Valid
    @NotEmpty(message = "Order must contain at least one item.")
    List<OrderItemRequestDto> items;

    private String notes;

}
