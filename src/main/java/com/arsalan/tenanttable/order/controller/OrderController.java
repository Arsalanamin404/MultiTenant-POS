package com.arsalan.tenanttable.order.controller;

import com.arsalan.tenanttable.common.dto.ApiResponse;
import com.arsalan.tenanttable.order.dto.*;
import com.arsalan.tenanttable.order.service.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(
        name = "Orders",
        description = "Create and manage customer orders, order items, discounts, and order lifecycle."
)
public class OrderController {
    private final IOrderService orderService;

    @PostMapping
    @Operation(
            summary = "Create a new order",
            description = "Creates a new order for a dining table and initializes it with the provided menu items."
    )
    public ResponseEntity<ApiResponse<OrderResponseDto>> createOrder(
            @Valid @RequestBody @Schema CreateOrderRequestDto dto,
            HttpServletRequest request
    ) {
        OrderResponseDto order = orderService.createOrder(dto);

        ApiResponse<OrderResponseDto> apiResponse = ApiResponse
                .success(HttpStatus.CREATED.value(),
                        "ORDER_CREATED_SUCCESSFULLY",
                        order,
                        request.getRequestURI()
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get order by ID",
            description = "Retrieves complete details of a specific order, including items, totals, discounts, and current status."
    )
    public ResponseEntity<ApiResponse<OrderResponseDto>> getOrder(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {
        OrderResponseDto order = orderService.getOrder(id);

        ApiResponse<OrderResponseDto> apiResponse = ApiResponse
                .success(HttpStatus.OK.value(),
                        "ORDER_FETCHED_SUCCESSFULLY",
                        order,
                        request.getRequestURI()
                );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/active")
    @Operation(
            summary = "List active orders",
            description = "Returns a paginated list of orders that are currently active (not completed or cancelled)."
    )
    public ResponseEntity<ApiResponse<Page<OrderResponseDto>>> getActiveOrders(
            Pageable pageable,
            HttpServletRequest request
    ) {
        Page<OrderResponseDto> activeOrders = orderService.getActiveOrders(pageable);

        ApiResponse<Page<OrderResponseDto>> apiResponse = ApiResponse
                .success(HttpStatus.OK.value(),
                        "ACTIVE_ORDERS_FETCHED_SUCCESSFULLY",
                        activeOrders,
                        request.getRequestURI()
                );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/history")
    @Operation(
            summary = "Get order history",
            description = "Returns a paginated list of completed and historical orders for the authenticated tenant."
    )
    public ResponseEntity<ApiResponse<Page<OrderResponseDto>>> getOrderHistory(
            Pageable pageable,
            HttpServletRequest request
    ) {
        Page<OrderResponseDto> orderHistory = orderService.getOrderHistory(pageable);

        ApiResponse<Page<OrderResponseDto>> apiResponse = ApiResponse
                .success(HttpStatus.OK.value(),
                        "ORDER_HISTORY_FETCHED_SUCCESSFULLY",
                        orderHistory,
                        request.getRequestURI()
                );

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{orderId}/items")
    @Operation(
            summary = "Add item to order",
            description = "Adds a menu item to an existing active order. If the item already exists, its quantity is increased."
    )
    public ResponseEntity<ApiResponse<OrderResponseDto>> addItem(
            @PathVariable UUID orderId,
            @Valid @RequestBody @Schema OrderItemRequestDto dto,
            HttpServletRequest request
    ) {
        OrderResponseDto order = orderService.addItem(orderId,dto);

        ApiResponse<OrderResponseDto> apiResponse = ApiResponse
                .success(HttpStatus.OK.value(),
                        "ITEM_ADDED_TO_CURRENT_ORDER_SUCCESSFULLY",
                        order,
                        request.getRequestURI()
                );

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{orderId}/items")
    @Operation(
            summary = "Update item quantity",
            description = "Updates the quantity of an existing item in an active order and recalculates the order totals."
    )
    public ResponseEntity<ApiResponse<OrderResponseDto>> updateItemQuantity(
            @PathVariable UUID orderId,
            @Valid @RequestBody @Schema UpdateOrderItemQuantityRequestDto dto,
            HttpServletRequest request
    ) {
        OrderResponseDto order = orderService.updateItemQuantity(orderId,dto);

        ApiResponse<OrderResponseDto> apiResponse = ApiResponse
                .success(HttpStatus.OK.value(),
                        "ITEM_QUANTITY_UPDATED_TO_CURRENT_ORDER_SUCCESSFULLY",
                        order,
                        request.getRequestURI()
                );

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{orderId}/items/{orderItemId}")
    @Operation(
            summary = "Remove item from order",
            description = "Removes a specific item from an active order and updates the order totals."
    )
    public ResponseEntity<ApiResponse<OrderResponseDto>> removeItem(
            @PathVariable UUID orderId,
            @PathVariable UUID orderItemId,
            HttpServletRequest request
    ) {
        OrderResponseDto order = orderService.removeItem(orderId,orderItemId);

        ApiResponse<OrderResponseDto> apiResponse = ApiResponse
                .success(HttpStatus.OK.value(),
                        "ITEM_REMOVED_FROM_CURRENT_ORDER_SUCCESSFULLY",
                        order,
                        request.getRequestURI()
                );

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{orderId}/discount")
    @Operation(
            summary = "Apply discount",
            description = "Applies a discount to the order and recalculates the final payable amount."
    )
    public ResponseEntity<ApiResponse<OrderResponseDto>> applyDiscount(
            @PathVariable UUID orderId,
            @Valid @RequestBody @Schema ApplyDiscountRequestDto dto,
            HttpServletRequest request
            ) {

        OrderResponseDto order = orderService.applyDiscount(orderId,dto);

        ApiResponse<OrderResponseDto> apiResponse = ApiResponse
                .success(HttpStatus.OK.value(),
                        "DISCOUNT_APPLIED_ON_CURRENT_ORDER_SUCCESSFULLY",
                        order,
                        request.getRequestURI()
                );

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{orderId}/status")
    @Operation(
            summary = "Change order status",
            description = "Updates the current status of an order (for example: PREPARING, SERVED, COMPLETED, or CANCELLED)."
    )
    public ResponseEntity<ApiResponse<OrderResponseDto>> changeStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody @Schema UpdateOrderStatusRequestDto dto,
            HttpServletRequest request) {

        OrderResponseDto order = orderService.changeStatus(orderId,dto);

        ApiResponse<OrderResponseDto> apiResponse = ApiResponse
                .success(HttpStatus.OK.value(),
                        "ORDER_STATUS_CHANGED_SUCCESSFULLY to"+dto.getStatus().toString(),
                        order,
                        request.getRequestURI()
                );

        return ResponseEntity.ok(apiResponse);
    }

}
