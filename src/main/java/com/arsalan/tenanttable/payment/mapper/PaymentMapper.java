package com.arsalan.tenanttable.payment.mapper;

import com.arsalan.tenanttable.payment.dto.PaymentResponseDto;
import com.arsalan.tenanttable.payment.entity.Payment;

public final class PaymentMapper {
    private PaymentMapper() {
    }

    public static PaymentResponseDto toDto(Payment payment) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .receivedById(payment.getReceivedBy().getId())
                .receivedByName(payment.getReceivedBy().getFullName())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
