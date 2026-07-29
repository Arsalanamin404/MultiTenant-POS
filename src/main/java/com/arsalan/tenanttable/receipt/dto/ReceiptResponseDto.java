package com.arsalan.tenanttable.receipt.dto;

import com.arsalan.tenanttable.payment.enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
@Data
public class ReceiptResponseDto {

    private long receiptNumber;

    private long orderNumber;

    private Instant paymentDate;

    private String diningTable;

    private String cashier;

    private BusinessInfoDto businessInfo;

    private List<ReceiptItemDto> items;

    private BigDecimal subtotal;

    private String coupon;
    private BigDecimal couponDiscountAmount;

    private BigDecimal taxRate;
    private BigDecimal taxAmount;

    private BigDecimal totalAmount;

    private PaymentMethod paymentMethod;
}
