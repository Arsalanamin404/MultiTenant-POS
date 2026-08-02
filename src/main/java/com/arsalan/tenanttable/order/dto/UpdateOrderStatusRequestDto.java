package com.arsalan.tenanttable.order.dto;

import com.arsalan.tenanttable.order.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderStatusRequestDto {

    @NotNull(message = "Status is required.")
    private OrderStatus status;
}
