package com.arsalan.tenanttable.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequestDto {
    @NotNull(message = "Dining table is required.")
    private UUID diningTableId;

    @Valid
    @NotEmpty(message = "Order must contain at least one item.")
    private List<OrderItemRequestDto> items;

    @Size(max = 300)
    private String notes;

}
