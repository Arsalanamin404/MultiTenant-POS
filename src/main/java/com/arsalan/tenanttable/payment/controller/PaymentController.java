package com.arsalan.tenanttable.payment.controller;

import com.arsalan.tenanttable.common.dto.ApiResponse;
import com.arsalan.tenanttable.payment.dto.CreatePaymentRequestDto;
import com.arsalan.tenanttable.payment.dto.PaymentResponseDto;
import com.arsalan.tenanttable.payment.service.IPaymentService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/payments")
@Tag(
        name = "Payments",
        description = "Create and manage customer payments"
)
@RequiredArgsConstructor
public class PaymentController {
    private final IPaymentService paymentService;

    @PostMapping
    @Operation(
            summary = "Record a payment",
            description = "Records a payment for an existing order and marks the order as completed."
    )
    public ResponseEntity<ApiResponse<PaymentResponseDto>> create(
            @Valid @RequestBody CreatePaymentRequestDto dto,
            HttpServletRequest request
    ) {

        PaymentResponseDto payment = paymentService.create(dto);

        ApiResponse<PaymentResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.CREATED.value(),
                "PAYMENT_RECORD_CREATED_SUCCESSFULLY",
                payment,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);

    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get payment by ID",
            description = "Retrieves a payment by its unique identifier."
    )
    public ResponseEntity<ApiResponse<PaymentResponseDto>> getById(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {
        PaymentResponseDto payment = paymentService.getById(id);
        ApiResponse<PaymentResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "PAYMENT_RECORD_FETCHED_SUCCESSFULLY",
                payment,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/order/{orderId}")
    @Operation(
            summary = "Get payment by order ID",
            description = "Retrieves the payment associated with a specific order."
    )
    public ResponseEntity<ApiResponse<PaymentResponseDto>> getByOrderId(
            @PathVariable UUID orderId,
            HttpServletRequest request
    ) {
        PaymentResponseDto payment = paymentService.getByOrderId(orderId);
        ApiResponse<PaymentResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "PAYMENT_RECORD_FETCHED_SUCCESSFULLY",
                payment,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    @Operation(
            summary = "Get all payments",
            description = "Retrieves a paginated list of payments for the current tenant."
    )
    public ResponseEntity<ApiResponse<Page<PaymentResponseDto>>> getAll(
            Pageable pageable,
            HttpServletRequest request
    ) {
        Page<PaymentResponseDto> payments = paymentService.getAll(pageable);

        ApiResponse<Page<PaymentResponseDto>> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "PAYMENT_RECORDS_FETCHED_SUCCESSFULLY",
                payments,
                request.getRequestURI()
        );
        return ResponseEntity.ok(apiResponse);
    }
}
