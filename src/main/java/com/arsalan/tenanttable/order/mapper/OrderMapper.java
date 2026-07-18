package com.arsalan.tenanttable.order.mapper;

import com.arsalan.tenanttable.order.dto.OrderResponseDto;
import com.arsalan.tenanttable.order.entity.Order;

public final class OrderMapper {
    private OrderMapper() {
    }

    public static OrderResponseDto toDto(Order order) {
        return OrderResponseDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .diningTableId(order.getDiningTable().getId())
                .diningTableNumber(order.getDiningTable().getTableNumber())
                .status(order.getStatus())
                .items(
                        order.getItems()
                                .stream()
                                .map(OrderItemMapper::toDto)
                                .toList()
                )
                .subTotal(order.getSubTotal())
                .taxRate(order.getTaxRate())
                .taxAmount(order.getTaxAmount())
                .discountRate(order.getDiscountRate())
                .discountAmount(order.getDiscountAmount())
                .totalAmount(order.getTotalAmount())
                .notes(order.getNotes())
                .createdByName(order.getCreatedBy().getFullName())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
