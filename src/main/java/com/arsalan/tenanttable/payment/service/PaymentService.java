package com.arsalan.tenanttable.payment.service;

import com.arsalan.tenanttable.common.utils.ICurrentUserUtilService;
import com.arsalan.tenanttable.exception.ResourceAlreadyExistsException;
import com.arsalan.tenanttable.exception.ResourceNotFoundException;
import com.arsalan.tenanttable.order.entity.Order;
import com.arsalan.tenanttable.order.enums.OrderStatus;
import com.arsalan.tenanttable.order.repository.OrderRepository;
import com.arsalan.tenanttable.payment.dto.CreatePaymentRequestDto;
import com.arsalan.tenanttable.payment.dto.PaymentResponseDto;
import com.arsalan.tenanttable.payment.entity.Payment;
import com.arsalan.tenanttable.payment.enums.PaymentMethod;
import com.arsalan.tenanttable.payment.mapper.PaymentMapper;
import com.arsalan.tenanttable.payment.repository.PaymentRepository;
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

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService implements IPaymentService {
    private final PaymentRepository paymentRepository;
    private final ICurrentUserUtilService currentUserUtilService;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final OrderRepository orderRepository;

    private User getOrThrowCurrentUser() {
        UUID userId = currentUserUtilService.getCurrentUserId();

        return userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND with user_id: " + userId));
    }

    private Tenant getOrThrowCurrentTenant() {
        UUID tenantId = currentUserUtilService.getCurrentTenantId();

        return tenantRepository
                .findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("TENANT_NOT_FOUND with tenant_id: " + tenantId));
    }

    private Order getOrThrowOrder(UUID orderId, Tenant tenant) {
        return orderRepository
                .findByIdAndTenant(orderId, tenant).
                orElseThrow(() -> new ResourceNotFoundException("ORDER_NOT_FOUND with order_id: " + orderId));
    }

    @Override
    @Transactional
    public PaymentResponseDto create(CreatePaymentRequestDto dto) {
        User currentUser = getOrThrowCurrentUser();
        Tenant currentTenant = getOrThrowCurrentTenant();

        Order order = getOrThrowOrder(dto.getOrderId(), currentTenant);

        if (paymentRepository.existsByOrder(order)) {
            throw new ResourceAlreadyExistsException("Payment already exists for this order");
        }

        if (dto.getPaymentMethod() != PaymentMethod.CASH &&
                (dto.getTransactionReference() == null ||
                        dto.getTransactionReference().isBlank())) {

            throw new IllegalArgumentException("Transaction reference is required.");
        }

        Payment payment = Payment.builder()
                .tenant(currentTenant)
                .order(order)
                .amount(order.getTotalAmount())
                .paymentMethod(dto.getPaymentMethod())
                .transactionReference(dto.getTransactionReference())
                .paidAt(Instant.now())
                .receivedBy(currentUser)
                .build();

        order.changeStatus(OrderStatus.COMPLETED);
        order.getDiningTable().makeAvailable(currentUser);

        payment = paymentRepository.save(payment);
        orderRepository.save(order);

        log.info(
                "Payment recorded. orderId={}, paymentId={}, amount={}, method={}",
                order.getId(),
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentMethod()
        );

        return PaymentMapper.toDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getById(UUID id) {
        Tenant currentTenant = getOrThrowCurrentTenant();

        Payment payment = paymentRepository
                .findByIdAndTenant(id, currentTenant)
                .orElseThrow(() -> new ResourceNotFoundException("PAYMENT_RECORD_NOT_FOUND with payment_id: " + id));

        return PaymentMapper.toDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getByOrderId(UUID orderId) {
        Tenant currentTenant = getOrThrowCurrentTenant();
        Order order = getOrThrowOrder(orderId, currentTenant);

        Payment payment = paymentRepository
                .findByOrder(order)
                .orElseThrow(() -> new ResourceNotFoundException("PAYMENT_RECORD_NOT_FOUND for order_id: " + orderId));

        return PaymentMapper.toDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponseDto> getAll(Pageable pageable) {
        Tenant currentTenant = getOrThrowCurrentTenant();

        return paymentRepository.
                findAllByTenant(currentTenant, pageable)
                .map(PaymentMapper::toDto);
    }
}
