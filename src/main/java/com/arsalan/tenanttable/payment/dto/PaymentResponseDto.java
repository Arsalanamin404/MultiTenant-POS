package com.arsalan.tenanttable.payment.dto;

import com.arsalan.tenanttable.payment.enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PaymentResponseDto {
    private UUID id;
    private UUID orderId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private UUID receivedById;
    private String receivedByName;
    private Instant createdAt;
}
