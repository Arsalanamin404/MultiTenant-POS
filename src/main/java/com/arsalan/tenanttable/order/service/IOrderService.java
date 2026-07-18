package com.arsalan.tenanttable.order.service;

import com.arsalan.tenanttable.order.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IOrderService {
    OrderResponseDto createOrder(CreateOrderRequestDto dto);

    OrderResponseDto getOrder(UUID id);

    Page<OrderResponseDto> getActiveOrders(Pageable pageable);

    Page<OrderResponseDto> getOrderHistory(Pageable pageable);

    OrderResponseDto addItem(UUID orderId, OrderItemRequestDto dto);

    OrderResponseDto updateItemQuantity(UUID orderId, UpdateOrderItemQuantityRequestDto dto);

    OrderResponseDto removeItem(UUID orderId, UUID orderItemId);

    OrderResponseDto applyDiscount(UUID orderId, ApplyDiscountRequestDto dto);

    OrderResponseDto changeStatus(UUID orderId, UpdateOrderStatusRequestDto dto);

}
