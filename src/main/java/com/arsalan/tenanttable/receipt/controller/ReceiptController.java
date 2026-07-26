package com.arsalan.tenanttable.receipt.controller;

import com.arsalan.tenanttable.common.dto.ApiResponse;
import com.arsalan.tenanttable.receipt.dto.ReceiptResponseDto;
import com.arsalan.tenanttable.receipt.service.IReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/receipts")
@Tag(
        name = "Receipts",
        description = "Retrieve order receipts and generate printable PDF receipts."
)
public class ReceiptController {

    private final IReceiptService receiptService;

    @GetMapping("/{orderId}")
    @Operation(
            summary = "Get receipt",
            description = "Retrieves the receipt details for a paid order."
    )
    public ResponseEntity<ApiResponse<ReceiptResponseDto>> getReceipt(
            @PathVariable UUID orderId,
            HttpServletRequest request
    ) {

        ReceiptResponseDto receipt = receiptService.getReceipt(orderId);

        ApiResponse<ReceiptResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "RECEIPT_FETCHED_SUCCESSFULLY",
                receipt,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{orderId}/pdf")
    @Operation(
            summary = "Download receipt PDF",
            description = "Generates and returns a printable PDF receipt for a paid order."
    )
    public ResponseEntity<byte[]> downloadReceiptPdf(
            @PathVariable UUID orderId
    ) {

        ReceiptResponseDto receipt = receiptService.getReceipt(orderId);
        byte[] pdf = receiptService.generateReceiptPdf(orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.inline()
                        .filename("receipt-" + receipt.getReceiptNumber() + ".pdf")
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}