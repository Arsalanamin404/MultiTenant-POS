package com.arsalan.tenanttable.order.mapper;

import com.arsalan.tenanttable.order.dto.OrderItemResponseDto;
import com.arsalan.tenanttable.order.entity.OrderItem;

public final class OrderItemMapper {
    private OrderItemMapper(){}

    public static OrderItemResponseDto toDto(OrderItem item){
        return OrderItemResponseDto.builder()
                .id(item.getId())
                .menuItemId(item.getMenuItem().getId())
                .menuItemName(item.getMenuItem().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .notes(item.getNotes())
                .build();
    }
}
