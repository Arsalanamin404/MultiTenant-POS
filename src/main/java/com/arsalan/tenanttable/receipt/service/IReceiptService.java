package com.arsalan.tenanttable.receipt.service;

import com.arsalan.tenanttable.receipt.dto.ReceiptResponseDto;

import java.util.UUID;

public interface IReceiptService {

    ReceiptResponseDto getReceipt(UUID orderId);

    byte[] generateReceiptPdf(UUID orderId);
}