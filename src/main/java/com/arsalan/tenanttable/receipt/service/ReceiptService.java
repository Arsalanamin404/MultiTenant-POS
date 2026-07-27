package com.arsalan.tenanttable.receipt.service;

import com.arsalan.tenanttable.common.utils.ICurrentUserUtilService;
import com.arsalan.tenanttable.exception.InvalidOperationException;
import com.arsalan.tenanttable.exception.ResourceNotFoundException;
import com.arsalan.tenanttable.order.entity.Order;
import com.arsalan.tenanttable.order.repository.OrderRepository;
import com.arsalan.tenanttable.payment.entity.Payment;
import com.arsalan.tenanttable.payment.repository.PaymentRepository;
import com.arsalan.tenanttable.receipt.dto.BusinessInfoDto;
import com.arsalan.tenanttable.receipt.dto.ReceiptItemDto;
import com.arsalan.tenanttable.receipt.dto.ReceiptResponseDto;
import com.arsalan.tenanttable.receipt.pdf.ReceiptPdfGenerator;
import com.arsalan.tenanttable.settings.entity.Settings;
import com.arsalan.tenanttable.settings.repository.SettingsRepository;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReceiptService implements IReceiptService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final TenantRepository tenantRepository;
    private final SettingsRepository settingsRepository;
    private final ICurrentUserUtilService currentUserUtilService;
    private final ReceiptPdfGenerator receiptPdfGenerator;
    
    private Tenant getOrThrowCurrentTenant() {
        UUID tenantId = currentUserUtilService.getCurrentTenantId();

        return tenantRepository.findById(tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tenant not found."));
    }

    private Order getOrThrowOrder(UUID orderId, Tenant tenant) {
        return orderRepository
                .findByIdAndTenant(orderId, tenant)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found."));
    }

    private Payment getOrThrowPayment(UUID orderId, Tenant tenant) {
        return paymentRepository
                .findByOrderIdAndTenant(orderId, tenant)
                .orElseThrow(() ->
                        new InvalidOperationException("Receipt cannot be generated for an unpaid order."));
    }

    private Settings getOrThrowSettings(Tenant tenant) {
        return settingsRepository
                .findByTenantId(tenant.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Settings not found."
                        ));
    }

    @Override
    public ReceiptResponseDto getReceipt(UUID orderId) {
        Tenant tenant = getOrThrowCurrentTenant();
        Order order = getOrThrowOrder(orderId, tenant);
        Payment payment = getOrThrowPayment(orderId, tenant);
        Settings settings = getOrThrowSettings(tenant);

        BusinessInfoDto businessInfo = BusinessInfoDto.builder()
                .businessName(tenant.getName())
                .address(settings.getAddress())
                .phone(settings.getPhoneNumber())
                .email(settings.getEmail())
                .gstNumber(settings.getGstNumber())
                .build();

        List<ReceiptItemDto> items = order.getItems()
                .stream()
                .map(item -> ReceiptItemDto.builder()
                        .itemName(item.getMenuItem().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getLineTotal())
                        .build())
                .toList();

        return ReceiptResponseDto.builder()
                .receiptNumber(order.getOrderNumber())
                .orderNumber(order.getOrderNumber())
                .paymentDate(payment.getCreatedAt())
                .diningTable(order.getDiningTable().getTableNumber())
                .cashier(order.getCreatedBy().getFullName())
                .businessInfo(businessInfo)
                .items(items)
                .subtotal(order.getSubTotal())
                .discountRate(order.getDiscountRate())
                .discountAmount(order.getDiscountAmount())
                .taxRate(order.getTaxRate())
                .taxAmount(order.getTaxAmount())
                .totalAmount(order.getTotalAmount())
                .paymentMethod(payment.getPaymentMethod())
                .build();

    }

    @Override
    public byte[] generateReceiptPdf(UUID orderId) {
        ReceiptResponseDto receipt = getReceipt(orderId);
        return receiptPdfGenerator.generate(receipt);
    }
}
