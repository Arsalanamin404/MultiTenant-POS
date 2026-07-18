package com.arsalan.tenanttable.order.dto;

import com.arsalan.tenanttable.order.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequestDto {

    @NotNull(message = "Status is required.")
    private OrderStatus status;
}
