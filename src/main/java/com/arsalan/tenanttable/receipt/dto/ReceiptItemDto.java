package com.arsalan.tenanttable.receipt.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class ReceiptItemDto {

    private String itemName;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;
}
