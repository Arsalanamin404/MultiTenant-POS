package com.arsalan.tenanttable.order.service;

import com.arsalan.tenanttable.common.utils.ICurrentUserUtilService;
import com.arsalan.tenanttable.dining_table.entity.DiningTable;
import com.arsalan.tenanttable.dining_table.enums.DiningTableStatus;
import com.arsalan.tenanttable.dining_table.repository.DiningTableRepository;
import com.arsalan.tenanttable.exception.InvalidOperationException;
import com.arsalan.tenanttable.exception.ResourceNotFoundException;
import com.arsalan.tenanttable.menu.entity.MenuItem;
import com.arsalan.tenanttable.menu.repository.MenuRepository;
import com.arsalan.tenanttable.order.dto.*;
import com.arsalan.tenanttable.order.entity.Order;
import com.arsalan.tenanttable.order.entity.OrderItem;
import com.arsalan.tenanttable.order.enums.OrderStatus;
import com.arsalan.tenanttable.order.mapper.OrderMapper;
import com.arsalan.tenanttable.order.repository.OrderRepository;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.tenant.repository.TenantRepository;
import com.arsalan.tenanttable.user.entity.User;
import com.arsalan.tenanttable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements IOrderService {
    private final OrderRepository orderRepository;
    private final DiningTableRepository diningTableRepository;
    private final MenuRepository menuRepository;
    private final ICurrentUserUtilService currentUserUtilService;
    private final IOrderCalculationService orderCalculationService;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;

    private User getOrThrowCurrentUser() {
        UUID userId = currentUserUtilService.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "USER_NOT_FOUND with user_id: " + userId
                        ));
    }

    private Tenant getOrThrowCurrentTenant() {
        UUID tenantId = currentUserUtilService.getCurrentTenantId();
        return tenantRepository.findById(tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "TENANT_NOT_FOUND with tenant_id: " + tenantId
                        ));
    }

    private DiningTable getOrThrowDiningTable(UUID tableId, Tenant tenant) {
        return diningTableRepository
                .findByIdAndTenant(tableId, tenant)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "DINING_TABLE_NOT_FOUND with table_id: " + tableId
                        ));
    }

    private MenuItem getOrThrowMenuItem(UUID menuItemId, Tenant tenant) {
        return menuRepository
                .findByIdAndTenantId(menuItemId, tenant.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "MENU_ITEM_NOT_FOUND with menu_id: " + menuItemId
                        ));
    }

    private Order getOrThrowOrder(UUID orderId, Tenant tenant) {
        return orderRepository
                .findByIdAndTenant(orderId, tenant).
                orElseThrow(() -> new ResourceNotFoundException("ORDER_NOT_FOUND with order_id: " + orderId));
    }

    private OrderItem getOrThrowOrderItem(Order order, UUID orderItemId) {
        return order.getItems()
                .stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ORDER_ITEM_NOT_FOUND with id: " + orderItemId
                        ));
    }

    private void validateEditable(Order order) {
        if (!order.isEditable()) {
            throw new InvalidOperationException(
                    "Order can no longer be modified because it is " + order.getStatus()
            );
        }
    }

    @Override
    @Transactional
    public OrderResponseDto createOrder(CreateOrderRequestDto dto) {

        User currentUser = getOrThrowCurrentUser();
        Tenant currentTenant = getOrThrowCurrentTenant();
        DiningTable table = getOrThrowDiningTable(dto.getDiningTableId(), currentTenant);

        if (table.getStatus() != DiningTableStatus.AVAILABLE) {
            throw new InvalidOperationException("Table '" + table.getTableNumber() + "' is currently " + table.getStatus());
        }

        Long orderNumber = orderRepository.findTopByTenantOrderByOrderNumberDesc(currentTenant)
                .map(lastOrder -> lastOrder.getOrderNumber() + 1)
                .orElse(1L);

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .tenant(currentTenant)
                .diningTable(table)
                .taxRate(currentTenant.getTaxRate())
                .discountRate(BigDecimal.ZERO)
                .createdBy(currentUser)
                .updatedBy(currentUser)
                .notes(dto.getNotes())
                .build();

        for (OrderItemRequestDto itemRequestDto : dto.getItems()) {
            MenuItem menuItem = getOrThrowMenuItem(itemRequestDto.getMenuItemId(), currentTenant);

            if (!menuItem.isAvailable()) {
                throw new InvalidOperationException(
                        "Menu item '" + menuItem.getName() + "' is currently unavailable."
                );
            }

            BigDecimal unitPrice = menuItem.getPrice();

            OrderItem orderItem = OrderItem.builder()
                    .menuItem(menuItem)
                    .quantity(itemRequestDto.getQuantity())
                    .unitPrice(unitPrice)
                    .build();

            orderItem.calculateLineTotal();

            order.addItem(orderItem);
        }

        orderCalculationService.calculateTotal(order);

        table.occupy(currentUser);

        Order savedOrder = orderRepository.save(order);

        diningTableRepository.save(table);

        log.info(
                "Order #{} created successfully for tenant '{}' on table '{}'.",
                savedOrder.getOrderNumber(),
                currentTenant.getId(),
                table.getTableNumber()
        );

        return OrderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(UUID id) {
        Tenant currentTenant = getOrThrowCurrentTenant();

        Order order = getOrThrowOrder(id, currentTenant);

        return OrderMapper.toDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getActiveOrders(Pageable pageable) {
        Tenant currentTenant = getOrThrowCurrentTenant();
        Page<Order> activeOrders = orderRepository.findByTenantAndStatusIn(
                currentTenant,
                List.of(
                        OrderStatus.PENDING,
                        OrderStatus.CONFIRMED,
                        OrderStatus.PREPARING,
                        OrderStatus.READY,
                        OrderStatus.SERVED
                ),
                pageable
        );

        return activeOrders.map(OrderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrderHistory(Pageable pageable) {
        Tenant currentTenant = getOrThrowCurrentTenant();
        Page<Order> orderHistory = orderRepository.findByTenantAndStatusIn(
                currentTenant,
                List.of(
                        OrderStatus.CANCELLED,
                        OrderStatus.COMPLETED
                ),
                pageable
        );

        return orderHistory.map(OrderMapper::toDto);

    }

    @Override
    @Transactional
    public OrderResponseDto addItem(UUID orderId, OrderItemRequestDto dto) {
        User currentUser = getOrThrowCurrentUser();
        Tenant currentTenant = getOrThrowCurrentTenant();
        Order currentOrder = getOrThrowOrder(orderId, currentTenant);


        validateEditable(currentOrder);

        MenuItem menuItem = getOrThrowMenuItem(dto.getMenuItemId(), currentTenant);

        if (!menuItem.isAvailable()) {
            throw new InvalidOperationException(
                    "MENU_ITEM_CURRENTLY_UNAVAILABLE: " + menuItem.getName()
            );
        }

        Optional<OrderItem> existingItem = currentOrder.getItems()
                .stream()
                .filter(item -> item.getMenuItem().getId().equals(menuItem.getId()))
                .findFirst();


        if (existingItem.isPresent()) {
            OrderItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + dto.getQuantity());
            item.calculateLineTotal();
        } else {
            OrderItem item = OrderItem.builder()
                    .order(currentOrder)
                    .menuItem(menuItem)
                    .quantity(dto.getQuantity())
                    .unitPrice(menuItem.getPrice())
                    .build();

            item.calculateLineTotal();
            currentOrder.addItem(item);
        }

        currentOrder.setUpdatedBy(currentUser);
        orderCalculationService.calculateTotal(currentOrder);
        Order savedOrder = orderRepository.save(currentOrder);

        return OrderMapper.toDto(savedOrder);

    }

    @Override
    @Transactional
    public OrderResponseDto updateItemQuantity(UUID orderId, UpdateOrderItemQuantityRequestDto dto) {
        User currentUser = getOrThrowCurrentUser();
        Tenant currentTenant = getOrThrowCurrentTenant();

        Order currentOrder = getOrThrowOrder(orderId, currentTenant);

        validateEditable(currentOrder);

        OrderItem orderItem = getOrThrowOrderItem(currentOrder, dto.getOrderItemId());

        orderItem.setQuantity(dto.getQuantity());
        orderItem.calculateLineTotal();

        orderCalculationService.calculateTotal(currentOrder);
        currentOrder.setUpdatedBy(currentUser);

        Order savedOrder = orderRepository.save(currentOrder);

        log.info(
                "Updated quantity of item '{}' in order #{}.",
                orderItem.getMenuItem().getName(),
                savedOrder.getOrderNumber()
        );

        return OrderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponseDto removeItem(UUID orderId, UUID orderItemId) {
        User currentUser = getOrThrowCurrentUser();
        Tenant currentTenant = getOrThrowCurrentTenant();

        Order currentOrder = getOrThrowOrder(orderId, currentTenant);

        validateEditable(currentOrder);

        OrderItem orderItem = getOrThrowOrderItem(currentOrder, orderItemId);

        if (currentOrder.getItems().size() == 1) {
            throw new InvalidOperationException(
                    "Cannot remove the last item. Cancel the order instead."
            );
        }

        currentOrder.removeItem(orderItem);

        orderCalculationService.calculateTotal(currentOrder);

        currentOrder.setUpdatedBy(currentUser);

        Order savedOrder = orderRepository.save(currentOrder);

        log.info(
                "Removed item '{}' from order #{}.",
                orderItem.getMenuItem().getName(),
                savedOrder.getOrderNumber()
        );

        return OrderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponseDto applyDiscount(UUID orderId, ApplyDiscountRequestDto dto) {
        User currentUser = getOrThrowCurrentUser();
        Tenant currentTenant = getOrThrowCurrentTenant();
        Order currentOrder = getOrThrowOrder(orderId, currentTenant);

        validateEditable(currentOrder);

        currentOrder.setDiscountRate(dto.getDiscountRate());
        orderCalculationService.calculateTotal(currentOrder);
        currentOrder.setUpdatedBy(currentUser);

        Order savedOrder = orderRepository.save(currentOrder);

        return OrderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponseDto changeStatus(UUID orderId, UpdateOrderStatusRequestDto dto) {
        User currentUser = getOrThrowCurrentUser();
        Tenant currentTenant = getOrThrowCurrentTenant();
        Order currentOrder = getOrThrowOrder(orderId, currentTenant);

        OrderStatus previousStatus = currentOrder.getStatus();

        log.info("Requested status: {}", dto.getStatus());
        log.info("Current status before: {}", currentOrder.getStatus());

        currentOrder.changeStatus(dto.getStatus());

        log.info("Current status after: {}", currentOrder.getStatus());

        if (currentOrder.getStatus().isTerminal()) {
            currentOrder.getDiningTable().makeAvailable(currentUser);
        }

        currentOrder.setUpdatedBy(currentUser);
        Order savedOrder = orderRepository.save(currentOrder);

        log.info(
                "Order #{} status changed from {} to {}.",
                savedOrder.getOrderNumber(),
                previousStatus,
                savedOrder.getStatus()
        );
        return OrderMapper.toDto(savedOrder);
    }
}
